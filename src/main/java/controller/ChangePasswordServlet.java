package controller;

import util.PasswordUtil;
import dao.UserDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/changepassword")
public class ChangePasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("changepassword.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getUserPrincipal().getName(); // Get the logged-in username
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
            if (!userDAO.validateUserPassword(username, currentPassword)) {
                request.setAttribute("errorMessage", "Current password is incorrect.");
                request.getRequestDispatcher("changepassword.jsp").forward(request, response);
                return;
            }
            
            // Hash the new password and update it in the database
            String hashedNewPassword = PasswordUtil.hashPassword(newPassword);
            boolean updateSuccessful = userDAO.updateUserPassword(username, hashedNewPassword);

            if (updateSuccessful) {
                request.setAttribute("successMessage", "Password changed successfully.");
            } else {
                request.setAttribute("errorMessage", "Error updating password. Please try again.");
            }

            request.getRequestDispatcher("changepassword.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Unexpected error occurred. Please try again.");
            request.getRequestDispatcher("changepassword.jsp").forward(request, response);
        }
    }
}
