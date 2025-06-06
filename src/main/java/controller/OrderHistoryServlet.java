package controller;

import java.io.IOException;
import java.util.List;

import newdao.OrderDAO;
import newdao.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import newmodel.Order;
import newmodel.User;

@WebServlet("/orderhistory")
public class OrderHistoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = orderDAO.getUserIdByUsername(username);

        List<Order> orders = orderDAO.getOrdersByUserId(userId);
        request.setAttribute("orders", orders);

        // Fetch user object to check if user is logged in through Google
        User user = userDAO.getUserByUsername(username);
        request.setAttribute("user", user);

        RequestDispatcher dispatcher = request.getRequestDispatcher("orderhistory.jsp");
        dispatcher.forward(request, response);
    }
}
