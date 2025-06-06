package controller;

import util.PasswordUtil;
import util.CodeGenerator;
import util.EmailUtil;
import newmodel.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/changepassword")
public class ChangePasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final newdao.UserDAO newUserDAO = new newdao.UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("changepassword.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username"); // Get username from session

        if (username == null || username.isEmpty()) {
            request.setAttribute("errorMessage", "You must be logged in to change your password.");
            request.getRequestDispatcher("changepassword.jsp").forward(request, response);
            return;
        }

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String repeatNewPassword = request.getParameter("repeatNewPassword");

        try {
            // Validate new passwords match
            if (!newPassword.equals(repeatNewPassword)) {
                request.setAttribute("errorMessage", "New passwords do not match.");
                request.getRequestDispatcher("changepassword.jsp").forward(request, response);
                return;
            }

            // Check if current password is correct
            if (!newUserDAO.checkPassword(username, currentPassword)) {
                request.setAttribute("errorMessage", "Current password is incorrect.");
                request.getRequestDispatcher("changepassword.jsp").forward(request, response);
                return;
            }

            // Hash the new password
            String hashedNewPassword = PasswordUtil.hashPassword(newPassword);

            // Get user email from newUserDAO
            User user = newUserDAO.getUserByUsername(username);
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                request.setAttribute("errorMessage", "Could not find email for verification.");
                request.getRequestDispatcher("changepassword.jsp").forward(request, response);
                return;
            }

            // Generate verification code
            String verificationCode = CodeGenerator.generateVerificationCode();

            // Store password and verification code in session
            session.setAttribute("pendingPasswordChange", true);
            session.setAttribute("newHashedPassword", hashedNewPassword);
            session.setAttribute("verificationCode", verificationCode);
            session.setAttribute("username", username); // Ensure username is in session

            // Send verification email
            EmailUtil.sendVerificationEmail(user.getEmail(), verificationCode);

            // Redirect to verify page
            response.sendRedirect("verify");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unexpected error occurred. Please try again.");
            request.getRequestDispatcher("changepassword.jsp").forward(request, response);
        }
    }
}
