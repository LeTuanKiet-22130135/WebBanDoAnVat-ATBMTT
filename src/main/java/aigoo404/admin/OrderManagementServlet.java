package aigoo404.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import newdao.JdbiConfig;
import newdao.OrderDAO;
import newdao.PubkeyDAO;
import newdao.UserDAO;
import newmodel.Order;
import newmodel.Pubkey;
import org.jdbi.v3.core.Jdbi;
import util.SignatureUtil;
import util.VnPayUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/orderManagementServlet")
public class OrderManagementServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PubkeyDAO pubkeyDAO = new PubkeyDAO();
    private final Jdbi jdbi = JdbiConfig.getJdbi();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get all orders with username
        List<Map<String, Object>> ordersList = getAllOrdersWithUsername();

        // Set content type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Convert the list of orders to JSON
        Gson gson = new Gson();
        String json = gson.toJson(ordersList);

        // Write the JSON to the response
        response.getWriter().write(json);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject jsonResponse = new JsonObject();

        // Check if content type is application/json
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            // Get action from request parameter
            String action = request.getParameter("action");

            if (action == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Action parameter is required");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            // Read request body for JSON requests
            BufferedReader reader = request.getReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            // Parse JSON
            Gson gson = new Gson();
            JsonObject requestData = gson.fromJson(sb.toString(), JsonObject.class);

            switch (action) {
                case "edit":
                    // Handle edit order request
                    handleEditOrder(requestData, response, jsonResponse);
                    break;
                default:
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Unknown action: " + action);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // Handle form submissions
            String action = request.getParameter("action");

            if (action == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Action parameter is required");
                response.getWriter().write(jsonResponse.toString());
                return;
            }

            switch (action) {
                case "checkIntegrity":
                    // Handle check integrity request
                    handleCheckIntegrity(request, response, jsonResponse);
                    break;
                default:
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Unknown action: " + action);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }

        response.getWriter().write(jsonResponse.toString());
    }

    private void handleEditOrder(JsonObject orderData, HttpServletResponse response, JsonObject jsonResponse) 
            throws IOException {
        try {
            int orderId = orderData.get("id").getAsInt();
            BigDecimal total = orderData.get("price").getAsBigDecimal();
            String payment = orderData.get("payment").getAsString();

            // Get the order
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Order not found");
                return;
            }

            // Update order
            order.setTotal(total);
            order.setPayment(payment);

            // Save changes
            boolean updatedPayment = orderDAO.updateOrderPayment(orderId, payment);

            // Update total price using direct SQL query
            boolean updatedTotal = jdbi.withHandle(handle -> {
                return handle.createUpdate("UPDATE orders SET total = :total WHERE id = :orderId")
                        .bind("total", total)
                        .bind("orderId", orderId)
                        .execute() > 0;
            });

            boolean updated = updatedPayment && updatedTotal;

            if (updated) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Order updated successfully");
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update order");
            }

        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error updating order: " + e.getMessage());
        }
    }

    private void handleCheckIntegrity(HttpServletRequest request, HttpServletResponse response, JsonObject jsonResponse) {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));

            // Get the order
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Order not found");
                return;
            }

            // Check if the order has a signature
            byte[] signature = order.getSignature();
            if (signature == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "This order does not have a signature");
                return;
            }

            // Check if the order has a pubkey_id
            Integer pubkeyId = order.getPubkeyId();
            if (pubkeyId == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "This order does not have a reference to the public key used for verification");
                return;
            }

            // Get the public key referenced by the pubkey_id in the order
            Pubkey pubkey = pubkeyDAO.getPubkeyById(pubkeyId);
            if (pubkey == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "The public key referenced by this order could not be found");
                return;
            }

            // Generate order information string to hash
            String orderInfo = SignatureUtil.generateOrderInfoString(order);

            // Generate SHA-1 hash
            String hash = VnPayUtil.sha1(orderInfo);

            // Verify signature
            boolean verified = SignatureUtil.verifySignature(hash.getBytes(), signature, pubkey.getPubkey());

            if (verified) {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Order integrity verified. The order data has not been compromised.");
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Order integrity check failed. The order data may have been compromised.");
            }

        } catch (NumberFormatException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid order ID format");
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error checking order integrity: " + e.getMessage());
        }
    }

    private List<Map<String, Object>> getAllOrdersWithUsername() {
        return jdbi.withHandle(handle -> {
            return handle.createQuery(
                    "SELECT o.id, o.uId, o.orderDate, o.total, o.verify, o.signature, o.payment, o.pubkey_id, u.username " +
                    "FROM orders o " +
                    "JOIN users u ON o.uId = u.id " +
                    "ORDER BY o.orderDate DESC")
                    .mapToMap()
                    .list();
        });
    }
}
