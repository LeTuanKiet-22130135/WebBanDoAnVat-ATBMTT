package controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import newdao.OrderDAO;
import newdao.PubkeyDAO;
import newdao.UserDAO;
import newmodel.Order;
import newmodel.Pubkey;
import newmodel.OrderDetail;
import util.SignatureUtil;
import util.VnPayUtil;

/**
 * Servlet for checking the integrity of an order using its stored signature
 */
@WebServlet("/checkintegrity")
public class CheckIntegrityServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PubkeyDAO pubkeyDAO = new PubkeyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get order ID from request
        String orderIdParam = request.getParameter("orderId");
        if (orderIdParam == null || orderIdParam.isEmpty()) {
            request.setAttribute("errorMessage", "Order ID not specified.");
            request.getRequestDispatcher("orderhistory").forward(request, response);
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdParam);

            // Get user ID
            int userId = userDAO.getUserIdByUsername(username);

            // Get the order
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                request.setAttribute("errorMessage", "Order not found.");
                request.getRequestDispatcher("orderhistory").forward(request, response);
                return;
            }

            // Verify that the order belongs to the logged-in user
            if (order.getUserId() != userId) {
                request.setAttribute("errorMessage", "Access denied. This order does not belong to you.");
                request.getRequestDispatcher("orderhistory").forward(request, response);
                return;
            }

            // Check if the order has a signature
            byte[] signature = order.getSignature();
            if (signature == null) {
                request.setAttribute("errorMessage", "This order does not have a signature.");
                request.getRequestDispatcher("orderhistory").forward(request, response);
                return;
            }

            // Get the user's public key
            Pubkey pubkey = pubkeyDAO.getPubkeyByUserId(userId);
            if (pubkey == null || !pubkey.isAvailable()) {
                request.setAttribute("errorMessage", "You don't have an active public key.");
                request.getRequestDispatcher("orderhistory").forward(request, response);
                return;
            }

            // Generate order information string to hash
            String orderInfo = SignatureUtil.generateOrderInfoString(order);

            // Generate SHA-1 hash
            String hash = VnPayUtil.sha1(orderInfo);

            // Verify signature
            boolean verified = SignatureUtil.verifySignature(hash.getBytes(), signature, pubkey.getPubkey());

            if (verified) {
                request.setAttribute("successMessage", "Order integrity verified. The order data has not been compromised.");
            } else {
                request.setAttribute("errorMessage", "Order integrity check failed. The order data may have been compromised.");
            }

            request.getRequestDispatcher("orderhistory").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid order ID format.");
            request.getRequestDispatcher("orderhistory").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error checking order integrity: " + e.getMessage());
            request.getRequestDispatcher("orderhistory").forward(request, response);
        }
    }

}
