package admincontroller;

import dao.UserDAO;
import util.PasswordUtil;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/admin/EditPassword")
public class EditPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("id"));
        request.setAttribute("userId", userId);
        RequestDispatcher dispatcher = request.getRequestDispatcher("EditPassword.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int userId = Integer.parseInt(request.getParameter("userId"));
        String newPassword = request.getParameter("newPassword");

        try {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);
            // Update password in database
            boolean isUpdated = userDAO.updateUserPassword(userId, hashedPassword);

            if (isUpdated) {
                request.setAttribute("message", "Password updated successfully.");
            } else {
                request.setAttribute("error", "Failed to update password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while updating the password.");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("EditPassword.jsp");
        dispatcher.forward(request, response);
    }
}
