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

        // Get order ID from request
        String orderIdParam = request.getParameter("orderId");

        // Check if we're dealing with a pending order or an existing order
        if (orderIdParam == null || orderIdParam.isEmpty()) {
            // No order ID means we're dealing with a pending order (cart items)
            handlePendingOrder(request, response, session, username);
        } else {
            // We have an order ID, so we're dealing with an existing order
            handleExistingOrder(request, response, session, username, orderIdParam);
        }
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
        String orderInfo = generateCartInfoString(cart, username, paymentMethod, shippingCost);

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
            String orderInfo = generateOrderInfoString(order);

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

    /**
     * Generates a string representation of the order for hashing
     * 
     * @param order The order to generate string for
     * @return A string containing order details
     */
    private String generateOrderInfoString(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("OrderID:").append(order.getId()).append(";");
        sb.append("UserID:").append(order.getUserId()).append(";");
        sb.append("OrderDate:").append(order.getOrderDate()).append(";");
        sb.append("Total:").append(order.getTotal()).append(";");

        // Add order details
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            sb.append("OrderDetails:[");
            order.getOrderDetails().forEach(detail -> {
                sb.append("{");
                sb.append("ProductName:").append(detail.getProductName()).append(",");
                sb.append("VariantName:").append(detail.getVariantName()).append(",");
                sb.append("Quantity:").append(detail.getQuantity()).append(",");
                sb.append("Price:").append(detail.getPrice());
                sb.append("}");
            });
            sb.append("]");
        }

        return sb.toString();
    }

    /**
     * Generates a string representation of the cart for hashing
     * 
     * @param cart The cart to generate string for
     * @param username The username of the cart owner
     * @param paymentMethod The selected payment method
     * @param shippingCost The shipping cost
     * @return A string containing cart details
     */
    private String generateCartInfoString(Cart cart, String username, String paymentMethod, BigDecimal shippingCost) {
        StringBuilder sb = new StringBuilder();
        sb.append("Username:").append(username).append(";");
        sb.append("PaymentMethod:").append(paymentMethod).append(";");
        sb.append("Subtotal:").append(cart.getSubtotal()).append(";");
        sb.append("ShippingCost:").append(shippingCost).append(";");
        sb.append("Total:").append(cart.getSubtotal().add(shippingCost)).append(";");

        // Add cart items
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            sb.append("CartItems:[");
            cart.getItems().forEach(item -> {
                sb.append("{");
                sb.append("ProductName:").append(item.getProductName()).append(",");
                sb.append("VariantId:").append(item.getVariantId()).append(",");
                sb.append("Quantity:").append(item.getQuantity()).append(",");
                sb.append("Price:").append(item.getPrice());
                sb.append("}");
            });
            sb.append("]");
        }

        return sb.toString();
    }
}
