package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.CartItem;
import model.Order;
import model.OrderItem;

public class OrderDAO {

	private final Connection connection;

	public OrderDAO() {
		this.connection = DBConnection.getConnection();
	}

	public int createOrder(int userId, BigDecimal totalAmount, List<CartItem> cartItems) {
		String orderQuery = "INSERT INTO orders (user_id, total_amount) VALUES (?, ?)";
		String orderItemQuery = "INSERT INTO order_items (order_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";
		int orderId = -1;

		try {
			connection.setAutoCommit(false); // Start transaction

			// Insert order and get generated order ID
			PreparedStatement orderStmt = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS);
			PreparedStatement orderItemStmt = connection.prepareStatement(orderItemQuery);
			orderStmt.setInt(1, userId);
			orderStmt.setBigDecimal(2, totalAmount);
			orderStmt.executeUpdate();
			ResultSet rs = orderStmt.getGeneratedKeys();
			if (rs.next()) {
				orderId = rs.getInt(1);
			}

			// Insert order items
			for (CartItem item : cartItems) {
				orderItemStmt.setInt(1, orderId);
				orderItemStmt.setInt(2, item.getProductId());
				orderItemStmt.setInt(3, item.getQuantity());
				orderItemStmt.setBigDecimal(4, item.getProductPrice());
				orderItemStmt.setBigDecimal(5, item.getTotalPrice());
				orderItemStmt.addBatch();
			}
			orderItemStmt.executeBatch();

			connection.commit(); // Commit transaction
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				connection.rollback(); // Rollback on error
			} catch (SQLException rollbackEx) {
				rollbackEx.printStackTrace();
			}
		} finally {
			try {
				connection.setAutoCommit(true); // Restore default auto-commit behavior
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}

		return orderId;
	}
	
	// Method to retrieve orders by user ID
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String orderQuery = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        String orderItemsQuery = """
                SELECT oi.product_id, p.name AS product_name, oi.quantity, oi.price
                FROM order_items oi
                INNER JOIN product p ON oi.product_id = p.id
                WHERE oi.order_id = ?
                """;

        try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery)) {
            orderStmt.setInt(1, userId);
            try (ResultSet rs = orderStmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setTotalAmount(rs.getBigDecimal("total_amount"));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());

                    // Fetch order items for this order
                    try (PreparedStatement itemStmt = connection.prepareStatement(orderItemsQuery)) {
                        itemStmt.setInt(1, order.getId());
                        try (ResultSet itemRs = itemStmt.executeQuery()) {
                            List<OrderItem> items = new ArrayList<>();
                            while (itemRs.next()) {
                                OrderItem item = new OrderItem();
                                item.setProductId(itemRs.getInt("product_id"));
                                item.setProductName(itemRs.getString("product_name"));
                                item.setQuantity(itemRs.getInt("quantity"));
                                item.setPrice(itemRs.getBigDecimal("price"));
                                items.add(item);
                            }
                            order.setItems(items);
                        }
                    }
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // Method to retrieve user ID by username
    public int getUserIdByUsername(String username) {
        String query = "SELECT id FROM user WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no user is found
    }

}
