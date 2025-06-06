package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Random;

import newdao.UserDAO;
import newmodel.User;
import util.EmailUtil;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Forward to the forgot password page
        RequestDispatcher dispatcher = request.getRequestDispatcher("forgot-password.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get the username from the form
        String username = request.getParameter("username");
        
        // Validate the username
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username is required");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }
        
        // Check if the user exists
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);
        
        if (user == null) {
            request.setAttribute("errorMessage", "Username not found");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }
        
        // Check if the user has an email
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            request.setAttribute("errorMessage", "No email associated with this account");
            request.getRequestDispatcher("forgot-password.jsp").forward(request, response);
            return;
        }
        
        // Generate a verification code
        String verificationCode = generateVerificationCode();
        
        // Store the verification code and username in the session
        HttpSession session = request.getSession();
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("resetUsername", username);
        session.setAttribute("resetPasswordPending", true);
        
        // Send the verification code to the user's email
        EmailUtil.sendVerificationEmail(user.getEmail(), verificationCode);
        
        // Redirect to the verification page
        response.sendRedirect("verify-reset");
    }
    
    /**
     * Generates a random 6-digit verification code
     * 
     * @return A 6-digit verification code
     */
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
        return String.valueOf(code);
    }
}