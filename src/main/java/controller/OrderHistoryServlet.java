package controller;

import java.io.IOException;
import java.util.List;

import newdao.OrderDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import newmodel.Order;

@WebServlet("/orderhistory")
public class OrderHistoryServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderDAO orderDAO = new OrderDAO();

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

        RequestDispatcher dispatcher = request.getRequestDispatcher("orderhistory.jsp");
        dispatcher.forward(request, response);
    }
}
