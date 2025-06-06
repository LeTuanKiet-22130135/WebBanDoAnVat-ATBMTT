package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

import newdao.UserDAO;
import util.PasswordUtil;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if there's a pending password reset
        HttpSession session = request.getSession();
        Boolean resetPasswordPending = (Boolean) session.getAttribute("resetPasswordPending");
        String verificationCode = (String) session.getAttribute("verificationCode");
        
        if (resetPasswordPending == null || !resetPasswordPending || verificationCode == null) {
            // No pending password reset or verification not completed, redirect to forgot password page
            response.sendRedirect("forgot-password");
            return;
        }
        
        // Forward to the reset password page
        RequestDispatcher dispatcher = request.getRequestDispatcher("reset-password.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get the new password and confirmation from the form
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Get the session
        HttpSession session = request.getSession();
        
        // Get the username from the session
        String username = (String) session.getAttribute("resetUsername");
        Boolean resetPasswordPending = (Boolean) session.getAttribute("resetPasswordPending");
        
        // Validate the inputs
        if (password == null || password.trim().isEmpty() || 
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Password fields cannot be empty");
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            return;
        }
        
        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match");
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            return;
        }
        
        // Check if there's a pending password reset and username is available
        if (resetPasswordPending == null || !resetPasswordPending || username == null) {
            response.sendRedirect("forgot-password");
            return;
        }
        
        try {
            // Hash the new password
            String hashedPassword = PasswordUtil.hashPassword(password);
            
            // Update the password in the database
            UserDAO userDAO = new UserDAO();
            boolean updateSuccessful = userDAO.updateUserPassword(username, hashedPassword);
            
            if (updateSuccessful) {
                // Clear the session attributes
                session.removeAttribute("verificationCode");
                session.removeAttribute("resetUsername");
                session.removeAttribute("resetPasswordPending");
                
                // Set a success message and redirect to login
                session.setAttribute("successMessage", "Password has been reset successfully. Please login with your new password.");
                response.sendRedirect("login");
            } else {
                request.setAttribute("errorMessage", "Failed to update password. Please try again.");
                request.getRequestDispatcher("reset-password.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred. Please try again.");
            request.getRequestDispatcher("reset-password.jsp").forward(request, response);
        }
    }
}