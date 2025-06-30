package aigoo404.admin;

import com.google.gson.Gson;
import newmodel.User;
import newdao.UserDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/userManagement")
public class UserManagementServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<User> users = userDAO.getAllUsers();

        // Set content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Convert the list of users to JSON
        Gson gson = new Gson();
        String json = gson.toJson(users);

        // Write the JSON to the response
        response.getWriter().write(json);
    }
}
