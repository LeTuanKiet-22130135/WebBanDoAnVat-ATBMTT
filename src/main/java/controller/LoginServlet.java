package controller;

import java.io.IOException;
import java.io.Serial;

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

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;

	private static final int MAX_FAILED_ATTEMPTS = 5;
	private static final int LOCK_TIME_SECONDS = 30;

	private final UserDAO userDAO = new UserDAO();
	private final CartDAO cartDAO = new CartDAO();

	/**
	 * Increments the failed login attempts counter and locks the account if necessary
	 */
	private void incrementFailedLoginAttempts(HttpSession session, HttpServletRequest req) {
		Integer attempts = (Integer) session.getAttribute("failedLoginAttempts");
		if (attempts == null) {
			attempts = 1;
		} else {
			attempts++;
		}
		session.setAttribute("failedLoginAttempts", attempts);

		// If max attempts reached, lock the login
		if (attempts >= MAX_FAILED_ATTEMPTS) {
			long lockUntil = System.currentTimeMillis() + (LOCK_TIME_SECONDS * 1000);
			session.setAttribute("loginLockUntil", lockUntil);
			req.setAttribute("loginLocked", true);
			req.setAttribute("lockUntil", lockUntil);
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Check if login is currently locked
		HttpSession session = req.getSession();
		Long lockUntil = (Long) session.getAttribute("loginLockUntil");

		if (lockUntil != null && lockUntil > System.currentTimeMillis()) {
			long remainingSeconds = (lockUntil - System.currentTimeMillis()) / 1000;
			req.setAttribute("error", "Too many failed login attempts. Please try again in " + remainingSeconds + " seconds.");
			req.setAttribute("loginLocked", true);
			req.setAttribute("lockUntil", lockUntil);
		}

		req.getRequestDispatcher("login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();

		// Check if login is currently locked
		Long lockUntil = (Long) session.getAttribute("loginLockUntil");
		if (lockUntil != null && lockUntil > System.currentTimeMillis()) {
			long remainingSeconds = (lockUntil - System.currentTimeMillis()) / 1000;
			req.setAttribute("error", "Too many failed login attempts. Please try again in " + remainingSeconds + " seconds.");
			req.setAttribute("loginLocked", true);
			req.setAttribute("lockUntil", lockUntil);
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}

		String username = req.getParameter("username");
		String password = req.getParameter("password");

		// Validate input
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			req.setAttribute("error", "Username and password are required.");
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}

		// Check if user exists
		if (!userDAO.checkUser(username)) {
			incrementFailedLoginAttempts(session, req);
			req.setAttribute("error", "Invalid username or password.");
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}

		// Check if password is correct
		if (!userDAO.checkPassword(username, password)) {
			incrementFailedLoginAttempts(session, req);
			req.setAttribute("error", "Invalid username or password.");
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}

		// Authentication successful, reset failed login attempts
		session.removeAttribute("failedLoginAttempts");
		session.removeAttribute("loginLockUntil");

		// Get the User object after successful login
		newmodel.User authenticatedUser = userDAO.getUserByUsername(username);

		// Set user information in session
		session.setAttribute("username", username);
		session.setAttribute("userId", authenticatedUser.getId());
		session.setAttribute("auth", authenticatedUser); // Store the User object

		// Get user's cart and calculate total items
		Cart cart = cartDAO.getCartByUserId(authenticatedUser.getId());
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

		// Redirect based on user status
		if (authenticatedUser.getStatus() == 2 || authenticatedUser.getStatus() == 3) {
			resp.sendRedirect(req.getContextPath() + "/admin/adminDashboard.jsp");
		} else {
			resp.sendRedirect(req.getContextPath() + "/index.jsp");
		}
	}
}
