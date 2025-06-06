package controller;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import newdao.CartDAO;
import newdao.UserDAO;
import newmodel.Cart;
import newmodel.CartItem;
import newmodel.User;
import util.GoogleOAuthConfig;
import util.GoogleOAuthService;

@WebServlet(urlPatterns = {"/oauth/google", "/oauth2callback"})
public class GoogleOAuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private final UserDAO userDAO = new UserDAO();
    private final CartDAO cartDAO = new CartDAO();
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        
        if ("/oauth/google".equals(path)) {
            // Step 1: Initiate OAuth flow
            initiateOAuthFlow(req, resp);
        } else if ("/oauth2callback".equals(path)) {
            // Step 2: Handle callback from Google
            handleOAuthCallback(req, resp);
        }
    }
    
    /**
     * Initiates the OAuth flow by redirecting to Google's authorization page
     */
    private void initiateOAuthFlow(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Generate a unique state parameter to prevent CSRF attacks
        String state = GoogleOAuthService.generateState();
        
        // Store the state in the session for verification later
        HttpSession session = req.getSession();
        session.setAttribute("oauth_state", state);
        
        // Build the authorization URL and redirect
        String authUrl = GoogleOAuthConfig.buildAuthorizationUrl(state);
        resp.sendRedirect(authUrl);
    }
    
    /**
     * Handles the callback from Google after authorization
     */
    private void handleOAuthCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession();
        
        // Get the state parameter from the request
        String state = req.getParameter("state");
        
        // Get the stored state from the session
        String storedState = (String) session.getAttribute("oauth_state");
        
        // Verify the state parameter to prevent CSRF attacks
        if (state == null || !state.equals(storedState)) {
            req.setAttribute("error", "Invalid state parameter. Possible CSRF attack.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }
        
        // Clear the state from the session
        session.removeAttribute("oauth_state");
        
        // Get the authorization code from the request
        String code = req.getParameter("code");
        if (code == null || code.isEmpty()) {
            req.setAttribute("error", "Authorization code not received from Google.");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }
        
        try {
            // Exchange the authorization code for an access token
            String accessToken = GoogleOAuthService.exchangeCodeForToken(code);
            
            // Get user information using the access token
            Map<String, String> userInfo = GoogleOAuthService.getUserInfo(accessToken);
            
            // Extract user information
            String email = userInfo.get("email");
            String firstName = userInfo.get("given_name");
            String lastName = userInfo.get("family_name");
            
            if (email == null || email.isEmpty()) {
                req.setAttribute("error", "Email not received from Google.");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
                return;
            }
            
            // Check if the user already exists
            User user = userDAO.getUserByEmail(email);
            
            if (user == null) {
                // User doesn't exist, create a new one
                int userId = userDAO.addUserWithOAuth(email, firstName, lastName, accessToken);
                
                if (userId == -1) {
                    req.setAttribute("error", "Failed to create user account.");
                    req.getRequestDispatcher("login.jsp").forward(req, resp);
                    return;
                }
                
                // Get the newly created user
                user = userDAO.getUserByEmail(email);
            } else {
                // User exists, update the token
                userDAO.updateUserToken(user.getId(), accessToken);
                user.setToken(accessToken);
            }
            
            // Set username in session
            session.setAttribute("username", user.getUsername());
            
            // Get user's cart and calculate total items
            Cart cart = cartDAO.getCartByUserId(user.getId());
            session.setAttribute("cart", cart);
            session.setAttribute("cartSubtotal", cart.getSubtotal());
            
            // Calculate total items count and store in session
            int totalItems = 0;
            if (cart.getItems() != null) {
                for (CartItem item : cart.getItems()) {
                    totalItems += item.getQuantity();
                }
            }
            session.setAttribute("cartItemCount", totalItems);
            
            // Redirect to index page
            resp.sendRedirect(req.getContextPath() + "/index");
            
        } catch (IOException e) {
            e.printStackTrace();
            req.setAttribute("error", "Error during OAuth authentication: " + e.getMessage());
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}