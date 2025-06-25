package newdao;

import newmodel.Cart;
import newmodel.CartItem;
import repository.cart.CartRepository;
import repository.cart.CartItemRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Optional;

public class CartDAO {
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private static final Logger logger = LoggerFactory.getLogger(CartDAO.class);

    public CartDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.cartRepo = jdbi.onDemand(CartRepository.class);
        this.cartItemRepo = jdbi.onDemand(CartItemRepository.class);
    }

    // Record Definitions
    public record CartItemInfo(int id, int quantity) {}

    public record CartRow(
            int cartId, Timestamp createdAt, Timestamp updatedAt, int cartItemId, int productId,
            String productName, String productImage, int variantId, String variantName,
            BigDecimal productPrice, int quantity
    ) {}

    // Row Mappers
    public static class CartItemMapper implements RowMapper<CartItem> {
        @Override
        public CartItem map(ResultSet rs, StatementContext ctx) throws SQLException {
            CartItem item = new CartItem();
            item.setId(rs.getInt("cart_item_id"));
            item.setProductId(rs.getInt("product_id"));
            item.setVariantId(rs.getInt("variant_id"));
            item.setProductName(formatProductName(rs));
            item.setProductImageUrl(rs.getString("product_image"));
            item.setPrice(rs.getBigDecimal("product_price"));
            item.setQuantity(rs.getInt("quantity"));
            return item;
        }

        private String formatProductName(ResultSet rs) throws SQLException {
            String baseName = rs.getString("product_name");
            String variant = rs.getString("variant_name");
            return variant != null ? baseName + " - " + variant : baseName;
        }
    }

    public static class CartRowMapper implements RowMapper<CartRow> {
        @Override
        public CartRow map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new CartRow(
                    rs.getInt("cart_id"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getInt("cart_item_id"),
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("product_image"),
                    rs.getInt("variant_id"),
                    rs.getString("variant_name"),
                    rs.getBigDecimal("product_price"),
                    rs.getInt("quantity")
            );
        }
    }

    // Refactored DAO Methods
    public int getOrCreateCartId(int userId) throws SQLException {
        return cartRepo.findCartIdByUserId(userId)
                .orElseGet(() -> cartRepo.createCart(userId));
    }

    public List<CartItem> getCartItems(int cartId) throws SQLException {
        return cartItemRepo.getCartItems(cartId);
    }

    @Transaction
    public void addOrUpdateCartItem(int cartId, int productId, int variantId) throws SQLException {
        logger.debug("Adding item to cart - CartID: {}, ProductID: {}, VariantID: {}",
                cartId, productId, variantId);

        try {
            Optional<CartItemInfo> existingItem = cartItemRepo.findCartItem(cartId, productId, variantId);
            logger.debug("Existing item found: {}", existingItem.isPresent());

            if (existingItem.isPresent()) {
                CartItemInfo item = existingItem.get();
                logger.debug("Updating quantity - ID: {}, Current Qty: {}, New Qty: {}",
                        item.id(), item.quantity(), item.quantity() + 1);
                cartItemRepo.updateQuantity(item.id(), item.quantity() + 1);
            } else {
                logger.debug("Inserting new cart item");
                cartItemRepo.insertItem(cartId, productId, variantId, 1);
            }
            cartRepo.updateCartTimestamp(cartId);
            logger.info("Cart item added/updated successfully");
        } catch (Exception e) {
            logger.error("Error adding/updating cart item: {}", e.getMessage());
            logger.debug("Full exception: ", e);
            throw new SQLException("Failed to add/update cart item", e);
        }
    }

    public int getCartIdByUsername(String username) throws SQLException {
        return cartRepo.findCartIdByUsername(username).orElse(-1);
    }

    @Transaction
    public void updateCartItemQuantity(
            int cartId, int productId, int variantId, int quantityChange
    ) throws SQLException {
        if (cartItemRepo.updateItemQuantity(cartId, productId, variantId, quantityChange)) {
            cartRepo.updateCartTimestamp(cartId);
        }
    }

    @Transaction
    public void removeItemFromCart(int cartId, int productId, int variantId) throws SQLException {
        if (cartItemRepo.removeItem(cartId, productId, variantId)) {
            cartRepo.updateCartTimestamp(cartId);
        }
    }

    public Cart getCartByUserId(int userId) {
        List<CartRow> rows = cartRepo.getCartRowsByUserId(userId);
        System.out.println(rows.size() + " rows");
        if (rows.isEmpty()) return new Cart();

        CartRow first = rows.get(0);
        Cart cart = new Cart();
        cart.setId(first.cartId());
        cart.setUserId(userId);
        cart.setCreatedAt(first.createdAt());
        cart.setUpdatedAt(first.updatedAt());

        for (CartRow row : rows) {
            if (row.cartItemId() > 0) {
                cart.getItems().add(toCartItem(row)); // Safe to add now
            }
        }
        logger.debug("Found {} items in cart", cart.getItems().size());
        return cart;
    }

    private CartItem toCartItem(CartRow row) {
        CartItem item = new CartItem();
        item.setId(row.cartItemId());
        item.setCartId(row.cartId());
        item.setProductId(row.productId());
        item.setVariantId(row.variantId());
        item.setProductName(formatName(row.productName(), row.variantName()));
        item.setProductImageUrl(row.productImage());
        item.setPrice(row.productPrice());
        item.setQuantity(row.quantity());
        return item;
    }

    private String formatName(String productName, String variantName) {
        return variantName != null ? productName + " - " + variantName : productName;
    }

    @Transaction
    public void clearCart(int cartId) {
        cartItemRepo.clearCart(cartId);
        cartRepo.updateCartTimestamp(cartId);
    }

    // DDL method remains unchanged
    public void createCartTables() throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            assert conn != null;
            try (Statement stmt = conn.createStatement()) {

                // Create cart table
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
}