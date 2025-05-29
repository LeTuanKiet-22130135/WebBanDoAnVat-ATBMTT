package newdao;

import newmodel.Order;
import newmodel.OrderDetail;
import newmodel.Shipping;
import newmodel.CartItem;
import newmodel.Variant;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Order entities
 * Handles database operations related to orders and order details
 */
public class OrderDAO {

    /**
     * Gets a user ID by username
     * 
     * @param username The username to search for
     * @return The user ID if found, -1 otherwise
     */
    public int getUserIdByUsername(String username) {
        String query = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Gets all orders for a specific user
     * 
     * @param userId The user ID to get orders for
     * @return A list of Order objects
     */
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE uId = ? ORDER BY orderDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("uId"));
                    order.setOrderDate(rs.getDate("orderDate"));
                    order.setTotal(rs.getBigDecimal("total"));

                    // Get order details for this order
                    order.setOrderDetails(getOrderDetailsByOrderId(order.getId()));

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Gets all order details for a specific order
     * 
     * @param orderId The order ID to get details for
     * @return A list of OrderDetail objects
     */
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String query = "SELECT od.*, v.name AS variant_name, p.name AS product_name FROM orderdetails od " +
                       "JOIN variants v ON od.vId = v.id " +
                       "JOIN products p ON v.pId = p.id " +
                       "WHERE od.oId = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setId(rs.getInt("id"));
                    orderDetail.setOrderId(rs.getInt("oId"));
                    orderDetail.setVariantId(rs.getInt("vId"));
                    orderDetail.setQuantity(rs.getInt("quantity"));
                    orderDetail.setPrice(rs.getBigDecimal("price"));
                    orderDetail.setVariantName(rs.getString("variant_name"));
                    orderDetail.setProductName(rs.getString("product_name"));

                    orderDetails.add(orderDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderDetails;
    }

    /**
     * Creates a new order with order details
     * 
     * @param userId The ID of the user placing the order
     * @param totalAmount The total amount of the order
     * @param cartItems The items in the cart
     * @return The ID of the created order, or -1 if creation fails
     */
    public int createOrder(int userId, BigDecimal totalAmount, List<CartItem> cartItems) {
        String orderQuery = "INSERT INTO orders (uId, orderDate, total) VALUES (?, CURDATE(), ?)";
        String orderDetailQuery = "INSERT INTO orderdetails (oId, vId, quantity, price) VALUES (?, ?, ?, ?)";
        int orderId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Insert order and get generated order ID
            try (PreparedStatement orderStmt = conn.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, userId);
                orderStmt.setBigDecimal(2, totalAmount);
                orderStmt.executeUpdate();

                try (ResultSet rs = orderStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        return -1;
                    }
                }
            }

            // Insert order details
            try (PreparedStatement orderDetailStmt = conn.prepareStatement(orderDetailQuery)) {
                for (CartItem item : cartItems) {
                    orderDetailStmt.setInt(1, orderId);
                    orderDetailStmt.setInt(2, item.getVariantId());
                    orderDetailStmt.setInt(3, item.getQuantity());
                    orderDetailStmt.setBigDecimal(4, item.getPrice());
                    orderDetailStmt.addBatch();
                }
                orderDetailStmt.executeBatch();
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        return orderId;
    }

    /**
     * Adds shipping information for an order
     * 
     * @param orderId The ID of the order
     * @param status The status of the order (0 = placed, 1 = process, 2 = shipped, 3 = received, -1 = failed)
     * @param paymentStatus The payment status (0 = not yet, 1 = success, -1 = failed)
     * @return The ID of the created shipping record, or -1 if creation fails
     */
    public int addShipping(int orderId, int status, int paymentStatus) {
        String query = "INSERT INTO shipping (oId, status, paymentStat) VALUES (?, ?, ?)";
        int shippingId = -1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, orderId);
            stmt.setInt(2, status);
            stmt.setInt(3, paymentStatus);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    shippingId = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return shippingId;
    }
}
