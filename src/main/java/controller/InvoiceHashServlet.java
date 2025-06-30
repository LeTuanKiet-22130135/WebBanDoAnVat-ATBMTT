package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import newdao.OrderDAO;
import newmodel.Cart;
import newmodel.CartItem;
import newmodel.Order;
import util.VnPayUtil;
import util.SignatureUtil;

/**
 * Servlet for generating and downloading SHA-1 hash of order details
 * Works with both existing orders and pending orders (cart items)
 */
@WebServlet("/invoicehash")
public class InvoiceHashServlet extends HttpServlet {

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

        // Get order ID from session
        Integer pendingOrderId = (Integer) session.getAttribute("pendingOrderId");
        String orderIdParam = pendingOrderId != null ? pendingOrderId.toString() : "";

        // Always use existing order handler
        handleExistingOrder(request, response, session, username, orderIdParam);
    }

    /**
     * Handle generating hash for a pending order (cart items)
     */
    private void handlePendingOrder(HttpServletRequest request, HttpServletResponse response, 
                                   HttpSession session, String username) 
            throws ServletException, IOException {

        // Get cart from session
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cart is empty");
            return;
        }

        // Get payment method from request or session
        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            paymentMethod = (String) session.getAttribute("paymentMethod");
        }

        // Get shipping cost from session
        BigDecimal shippingCost = (BigDecimal) session.getAttribute("shippingCost");
        if (shippingCost == null) {
            shippingCost = BigDecimal.ZERO;
        }

        // Generate order information string to hash
        String orderInfo = SignatureUtil.generateOrderInfoString(orderDAO.getOrderById(Integer.parseInt(session.getAttribute("pendingOrderId").toString())));

        // Generate SHA-1 hash
        String hash = VnPayUtil.sha1(orderInfo);

        // Set response headers for file download
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"pending_order.hash\"");

        // Write hash to response
        try (PrintWriter out = response.getWriter()) {
            out.print(hash);
        }
    }

    /**
     * Handle generating hash for an existing order
     */
    private void handleExistingOrder(HttpServletRequest request, HttpServletResponse response, 
                                    HttpSession session, String username, String orderIdParam) 
            throws ServletException, IOException {

        try {
            int orderId = Integer.parseInt(orderIdParam);

            // Get order details
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
                return;
            }

            // Verify that the order belongs to the logged-in user
            int userId = orderDAO.getUserIdByUsername(username);
            if (order.getUserId() != userId) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }

            // Generate order information string to hash
            String orderInfo = SignatureUtil.generateOrderInfoString(order);

            // Generate SHA-1 hash (changed from SHA-256)
            String hash = VnPayUtil.sha1(orderInfo);

            // Set response headers for file download
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"order_" + orderId + ".hash\"");

            // Write hash to response
            try (PrintWriter out = response.getWriter()) {
                out.print(hash);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID format");
        }
    }

}
