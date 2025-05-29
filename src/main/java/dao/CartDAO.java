package dao;

import model.Cart;
import model.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

	private final Connection conn;

	public CartDAO() {
		this.conn = DBConnection.getConnection();
	}

	public int getOrCreateCartId(int userId) throws SQLException {
		String cartQuery = "SELECT id FROM Cart WHERE user_id = ?";
		try (PreparedStatement cartStmt = conn.prepareStatement(cartQuery)) {
			cartStmt.setInt(1, userId);
			try (ResultSet cartRs = cartStmt.executeQuery()) {
				if (cartRs.next()) {
					return cartRs.getInt("id");
				}
			}
		}

		// Create a new cart if none exists
		String createCartQuery = "INSERT INTO Cart (user_id, created_at) VALUES (?, NOW())";
		try (PreparedStatement createCartStmt = conn.prepareStatement(createCartQuery, Statement.RETURN_GENERATED_KEYS)) {
			createCartStmt.setInt(1, userId);
			createCartStmt.executeUpdate();
			try (ResultSet generatedKeys = createCartStmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					return generatedKeys.getInt(1);
				}
			}
		}
		return -1; // Return -1 if creation fails
	}

	public List<CartItem> getCartItems(int cartId) throws SQLException {
		List<CartItem> items = new ArrayList<>();
		String query = """
				SELECT ci.id AS cart_item_id, 
				       p.id AS product_id, 
				       p.name, 
				       p.image AS product_image, 
				       p.price AS product_price, 
				       ci.quantity 
				FROM CartItem ci
				INNER JOIN Product p ON ci.product_id = p.id
				WHERE ci.cart_id = ?;
				""";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, cartId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					CartItem item = new CartItem();
					item.setId(rs.getInt("cart_item_id"));
					item.setCartId(cartId);
					item.setProductId(rs.getInt("product_id"));
					item.setProductName(rs.getString("name"));
					item.setProductImageUrl(rs.getString("product_image"));
					item.setProductPrice(rs.getBigDecimal("product_price"));
					item.setQuantity(rs.getInt("quantity"));
					items.add(item);
				}
			}
		}
		return items;
	}

	public void addOrUpdateCartItem(int cartId, int productId) throws SQLException {
		String cartItemQuery = "SELECT id, quantity FROM CartItem WHERE cart_id = ? AND product_id = ?";
		try (PreparedStatement cartItemStmt = conn.prepareStatement(cartItemQuery)) {
			cartItemStmt.setInt(1, cartId);
			cartItemStmt.setInt(2, productId);
			try (ResultSet cartItemRs = cartItemStmt.executeQuery()) {
				if (cartItemRs.next()) {
					int existingQuantity = cartItemRs.getInt("quantity");
					String updateQuery = "UPDATE CartItem SET quantity = ? WHERE id = ?";
					try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
						updateStmt.setInt(1, existingQuantity + 1);
						updateStmt.setInt(2, cartItemRs.getInt("id"));
						updateStmt.executeUpdate();
					}
				} else {
					String insertQuery = "INSERT INTO CartItem (cart_id, product_id, quantity) VALUES (?, ?, ?)";
					try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
						insertStmt.setInt(1, cartId);
						insertStmt.setInt(2, productId);
						insertStmt.setInt(3, 1);
						insertStmt.executeUpdate();
					}
				}
			}
		}
	}

	// Fetch the cart ID for the given username
	public int getCartIdByUsername(String username) throws SQLException {
		String query = "SELECT c.id FROM Cart c INNER JOIN user u ON c.user_id = u.id WHERE u.username = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("id");
				}
			}
		}
		return -1; // Return -1 if no cart found
	}

	// Update cart item quantity (increase or decrease)
	public void updateCartItemQuantity(int cartId, int productId, int quantityChange) throws SQLException {
		String query = """
				    UPDATE CartItem 
				    SET quantity = quantity + ? 
				    WHERE cart_id = ? AND product_id = ? AND quantity + ? >= 1
				""";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, quantityChange);
			stmt.setInt(2, cartId);
			stmt.setInt(3, productId);
			stmt.setInt(4, quantityChange);
			stmt.executeUpdate();
		}
	}
	
	public void removeItemFromCart(int cartId, int productId) throws SQLException {
        String query = "DELETE FROM CartItem WHERE cart_id = ? AND product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
	
	public Cart getCartById(int userId) {
	    Cart cart = new Cart();
	    String query = """
	            SELECT ci.id AS cart_item_id, 
	                   p.id AS product_id, 
	                   p.name, 
	                   p.image AS product_image, 
	                   p.price AS product_price, 
	                   ci.quantity 
	            FROM CartItem ci
	            INNER JOIN Product p ON ci.product_id = p.id
	            INNER JOIN Cart c ON ci.cart_id = c.id
	            WHERE c.user_id = ?;
	            """;

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, userId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            List<CartItem> items = new ArrayList<>();
	            BigDecimal subtotal = BigDecimal.ZERO;

	            while (rs.next()) {
	                CartItem item = new CartItem();
	                item.setId(rs.getInt("cart_item_id"));
	                item.setCartId(cart.getId());
	                item.setProductId(rs.getInt("product_id"));
	                item.setProductName(rs.getString("name"));
	                item.setProductImageUrl(rs.getString("product_image"));
	                item.setProductPrice(rs.getBigDecimal("product_price"));
	                item.setQuantity(rs.getInt("quantity"));

	                items.add(item);
	                subtotal = subtotal.add(item.getTotalPrice());
	            }

	            cart.setItems(items);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return cart;
	}
	
	public void clearCart(int cartId) {
        String query = "DELETE FROM cartitem WHERE cart_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
}
