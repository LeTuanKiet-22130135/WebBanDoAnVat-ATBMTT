package repository.cart;

import newdao.CartDAO;
import newmodel.CartItem;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {
    @SqlQuery("""
            SELECT ci.id, ci.quantity
            FROM cart_items ci
            WHERE ci.cart_id = :cartId
              AND ci.product_id = :productId
              AND ci.variant_id = :variantId
            """)
    @RegisterConstructorMapper(CartDAO.CartItemInfo.class)
    Optional<CartDAO.CartItemInfo> findCartItem(
            @Bind("cartId") int cartId,
            @Bind("productId") int productId,
            @Bind("variantId") int variantId
    );

    @SqlUpdate("""
            UPDATE cart_items
            SET quantity = :quantity, updated_at = NOW()
            WHERE id = :id
            """)
    void updateQuantity(@Bind("id") int id, @Bind("quantity") int quantity);

    @SqlUpdate("""
            INSERT INTO cart_items
            (cart_id, product_id, variant_id, quantity, created_at, updated_at)
            VALUES (:cartId, :productId, :variantId, :quantity, NOW(), NOW())
            """)
    void insertItem(
            @Bind("cartId") int cartId,
            @Bind("productId") int productId,
            @Bind("variantId") int variantId,
            @Bind("quantity") int quantity
    );

    @SqlQuery("""
            SELECT ci.id AS cart_item_id, p.id AS product_id, p.name AS product_name,
                   p.img AS product_image, v.id AS variant_id, v.name AS variant_name,
                   v.price AS product_price, ci.quantity
            FROM cart_items ci
            INNER JOIN products p ON ci.product_id = p.id
            LEFT JOIN variants v ON ci.variant_id = v.id
            WHERE ci.cart_id = :cartId
            """)
    @RegisterRowMapper(CartDAO.CartItemMapper.class)
    List<CartItem> getCartItems(@Bind("cartId") int cartId);

    @SqlUpdate("""
            UPDATE cart_items
            SET quantity = quantity + :quantityChange, updated_at = NOW()
            WHERE cart_id = :cartId
              AND product_id = :productId
              AND variant_id = :variantId
              AND quantity + :quantityChange >= 1
            """)
    boolean updateItemQuantity(
            @Bind("cartId") int cartId,
            @Bind("productId") int productId,
            @Bind("variantId") int variantId,
            @Bind("quantityChange") int quantityChange
    );

    @SqlUpdate("""
            DELETE FROM cart_items
            WHERE cart_id = :cartId
              AND product_id = :productId
              AND variant_id = :variantId
            """)
    boolean removeItem(
            @Bind("cartId") int cartId,
            @Bind("productId") int productId,
            @Bind("variantId") int variantId
    );

    @SqlUpdate("DELETE FROM cart_items WHERE cart_id = :cartId")
    void clearCart(@Bind("cartId") int cartId);
}
