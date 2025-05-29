package newdao;

import newmodel.Cart;
import newmodel.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Cart entities
 * Handles database operations related to shopping carts
 */
public class CartDAO {

    /**
     * Get an existing cart ID for a user or create a new one
     * 
     * @param userId The ID of the user
     * @return The cart ID, or -1 if creation fails
     * @throws SQLException If a database error occurs
     */
    public int getOrCreateCartId(int userId) throws SQLException {
        String cartQuery = "SELECT id FROM carts WHERE user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement cartStmt = conn.prepareStatement(cartQuery)) {
            
            cartStmt.setInt(1, userId);
            try (ResultSet cartRs = cartStmt.executeQuery()) {
                if (cartRs.next()) {
                    return cartRs.getInt("id");
                }
            }
        
            // Create a new cart if none exists
            String createCartQuery = "INSERT INTO carts (user_id, created_at, updated_at) VALUES (?, NOW(), NOW())";
            try (PreparedStatement createCartStmt = conn.prepareStatement(createCartQuery, Statement.RETURN_GENERATED_KEYS)) {
                createCartStmt.setInt(1, userId);
                createCartStmt.executeUpdate();
                try (ResultSet generatedKeys = createCartStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        }
        
        return -1; // Return -1 if creation fails
    }

    /**
     * Get all items in a cart
     * 
     * @param cartId The ID of the cart
     * @return A list of cart items
     * @throws SQLException If a database error occurs
     */
    public List<CartItem> getCartItems(int cartId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        String query = """
                SELECT ci.id AS cart_item_id, 
                       p.id AS product_id, 
                       p.name AS product_name, 
                       p.img AS product_image, 
                       v.id AS variant_id,
                       v.name AS variant_name,
                       v.price AS product_price, 
                       ci.quantity 
                FROM cart_items ci
                INNER JOIN products p ON ci.product_id = p.id
                LEFT JOIN variants v ON ci.variant_id = v.id
                WHERE ci.cart_id = ?;
                """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("cart_item_id"));
                    item.setCartId(cartId);
                    item.setProductId(rs.getInt("product_id"));
                    item.setVariantId(rs.getInt("variant_id"));
                    item.setProductName(rs.getString("product_name") + 
                                       (rs.getString("variant_name") != null ? " - " + rs.getString("variant_name") : ""));
                    item.setProductImageUrl(rs.getString("product_image"));
                    item.setPrice(rs.getBigDecimal("product_price"));
                    item.setQuantity(rs.getInt("quantity"));
                    items.add(item);
                }
            }
        }
        
        return items;
    }

    /**
     * Add a new item to a cart or update its quantity
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product
     * @param variantId The ID of the product variant (optional, can be 0)
     * @throws SQLException If a database error occurs
     */
    public void addOrUpdateCartItem(int cartId, int productId, int variantId) throws SQLException {
        String cartItemQuery = "SELECT id, quantity FROM cart_items WHERE cart_id = ? AND product_id = ? AND variant_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement cartItemStmt = conn.prepareStatement(cartItemQuery)) {

            cartItemStmt.setInt(1, cartId);
            cartItemStmt.setInt(2, productId);
            cartItemStmt.setInt(3, variantId);

            try (ResultSet cartItemRs = cartItemStmt.executeQuery()) {
                if (cartItemRs.next()) {
                    // Update existing item quantity
                    int existingQuantity = cartItemRs.getInt("quantity");
                    String updateQuery = "UPDATE cart_items SET quantity = ?, updated_at = NOW() WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, existingQuantity + 1);
                        updateStmt.setInt(2, cartItemRs.getInt("id"));
                        updateStmt.executeUpdate();
                    }

                    // Update cart updated_at timestamp
                    updateCartTimestamp(conn, cartId);
                } else {
                    // Insert new item
                    String insertQuery = "INSERT INTO cart_items (cart_id, product_id, variant_id, quantity, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, cartId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, variantId);
                        insertStmt.setInt(4, 1);
                        insertStmt.executeUpdate();
                    }

                    // Update cart updated_at timestamp
                    updateCartTimestamp(conn, cartId);
                }
            }
        }
    }

    /**
     * Get a cart ID for a given username
     * 
     * @param username The username of the user
     * @return The cart ID, or -1 if no cart found
     * @throws SQLException If a database error occurs
     */
    public int getCartIdByUsername(String username) throws SQLException {
        String query = "SELECT c.id FROM carts c INNER JOIN users u ON c.user_id = u.id WHERE u.username = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        
        return -1; // Return -1 if no cart found
    }

    /**
     * Update cart item quantity
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product
     * @param variantId The ID of the product variant
     * @param quantityChange The change in quantity (positive or negative)
     * @throws SQLException If a database error occurs
     */
    public void updateCartItemQuantity(int cartId, int productId, int variantId, int quantityChange) throws SQLException {
        String query = """
                UPDATE cart_items 
                SET quantity = quantity + ?, updated_at = NOW() 
                WHERE cart_id = ? AND product_id = ? AND variant_id = ? AND quantity + ? >= 1
                """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, cartId);
            stmt.setInt(3, productId);
            stmt.setInt(4, variantId);
            stmt.setInt(5, quantityChange);
            stmt.executeUpdate();
            
            // Update cart updated_at timestamp
            updateCartTimestamp(conn, cartId);
        }
    }

    /**
     * Remove an item from a cart
     * 
     * @param cartId The ID of the cart
     * @param productId The ID of the product
     * @param variantId The ID of the product variant
     * @throws SQLException If a database error occurs
     */
    public void removeItemFromCart(int cartId, int productId, int variantId) throws SQLException {
        String query = "DELETE FROM cart_items WHERE cart_id = ? AND product_id = ? AND variant_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, cartId);
            stmt.setInt(2, productId);
            stmt.setInt(3, variantId);
            stmt.executeUpdate();
            
            // Update cart updated_at timestamp
            updateCartTimestamp(conn, cartId);
        }
    }

    /**
     * Get a cart by user ID
     * 
     * @param userId The ID of the user
     * @return The cart
     */
    public Cart getCartByUserId(int userId) {
        Cart cart = new Cart();
        String query = """
                SELECT c.id AS cart_id, c.created_at, c.updated_at,
                       ci.id AS cart_item_id, 
                       p.id AS product_id, 
                       p.name AS product_name, 
                       p.img AS product_image, 
                       v.id AS variant_id,
                       v.name AS variant_name,
                       v.price AS product_price, 
                       ci.quantity 
                FROM carts c
                LEFT JOIN cart_items ci ON c.id = ci.cart_id
                LEFT JOIN products p ON ci.product_id = p.id
                LEFT JOIN variants v ON ci.variant_id = v.id
                WHERE c.user_id = ?;
                """;
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<CartItem> items = new ArrayList<>();
                boolean cartInfoSet = false;
                
                while (rs.next()) {
                    if (!cartInfoSet) {
                        cart.setId(rs.getInt("cart_id"));
                        cart.setUserId(userId);
                        cart.setCreatedAt(rs.getTimestamp("created_at"));
                        cart.setUpdatedAt(rs.getTimestamp("updated_at"));
                        cartInfoSet = true;
                    }
                    
                    // Only add items if they exist (cart might be empty)
                    if (rs.getInt("cart_item_id") > 0) {
                        CartItem item = new CartItem();
                        item.setId(rs.getInt("cart_item_id"));
                        item.setCartId(cart.getId());
                        item.setProductId(rs.getInt("product_id"));
                        item.setVariantId(rs.getInt("variant_id"));
                        item.setProductName(rs.getString("product_name") + 
                                           (rs.getString("variant_name") != null ? " - " + rs.getString("variant_name") : ""));
                        item.setProductImageUrl(rs.getString("product_image"));
                        item.setPrice(rs.getBigDecimal("product_price"));
                        item.setQuantity(rs.getInt("quantity"));
                        items.add(item);
                    }
                }
                
                cart.setItems(items);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cart;
    }

    /**
     * Clear all items from a cart
     * 
     * @param cartId The ID of the cart
     */
    public void clearCart(int cartId) {
        String query = "DELETE FROM cart_items WHERE cart_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
            
            // Update cart updated_at timestamp
            updateCartTimestamp(conn, cartId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update the updated_at timestamp of a cart
     * 
     * @param conn The database connection
     * @param cartId The ID of the cart
     * @throws SQLException If a database error occurs
     */
    private void updateCartTimestamp(Connection conn, int cartId) throws SQLException {
        String query = "UPDATE carts SET updated_at = NOW() WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        }
    }

    /**
     * Create the necessary database tables for carts and cart items
     * 
     * @throws SQLException If a database error occurs
     */
    public void createCartTables() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create carts table
            String createCartsTable = """
                    CREATE TABLE IF NOT EXISTS carts (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        user_id INT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                    )
                    """;
            stmt.executeUpdate(createCartsTable);
            
            // Create cart_items table
            String createCartItemsTable = """
                    CREATE TABLE IF NOT EXISTS cart_items (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        cart_id INT NOT NULL,
                        product_id INT NOT NULL,
                        variant_id INT DEFAULT 0,
                        quantity INT NOT NULL DEFAULT 1,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
                        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
                        FOREIGN KEY (variant_id) REFERENCES variants(id) ON DELETE CASCADE,
                        UNIQUE KEY cart_product_variant (cart_id, product_id, variant_id)
                    )
                    """;
            stmt.executeUpdate(createCartItemsTable);
        }
    }
}