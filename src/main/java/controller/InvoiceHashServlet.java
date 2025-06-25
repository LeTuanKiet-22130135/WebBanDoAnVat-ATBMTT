package controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import newdao.OrderDAO;
import newmodel.Order;
import util.VnPayUtil;

/**
 * Servlet for generating and downloading SHA-256 hash of order details
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
        if (orderIdParam == null || orderIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID is required");
            return;
        }
        
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
            
            // Generate SHA-256 hash
            String hash = VnPayUtil.sha256(orderInfo);
            
            // Set response headers for file download
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"order_" + orderId + ".key\"");
            
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
}