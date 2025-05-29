package admincontroller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UserDAO;
import model.User;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/UserControl")
public class UserControlServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO(); // Instantiate DAO

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	String query = request.getParameter("query");

        List<User> users;
        if (query != null && !query.trim().isEmpty()) {
            // Search users by name
            users = userDAO.searchUsersByName(query);
        } else {
            // Fetch all users if no query is provided
            users = userDAO.getAllUsers();
        }
        
        request.setAttribute("users", users);
        request.getRequestDispatcher("UserControl.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int userId = Integer.parseInt(request.getParameter("userId"));

        if ("delete".equals(action)) {
            boolean success = userDAO.deleteUserById(userId);
            if (success) {
                request.setAttribute("message", "User deleted successfully!");
            } else {
                request.setAttribute("error", "Failed to delete user.");
            }
        } else if ("updateRole".equals(action)) {
            boolean isAdmin = Boolean.parseBoolean(request.getParameter("isAdmin"));
            boolean success = userDAO.updateUserRole(userId, isAdmin);
            if (success) {
                request.setAttribute("message", "User role updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update user role.");
            }
        }

        // Redirect to refresh the user list
        response.sendRedirect("UserControl");
    }
}
