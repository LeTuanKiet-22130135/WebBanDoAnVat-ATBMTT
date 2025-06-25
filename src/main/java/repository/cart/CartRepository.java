package repository.cart;

import newdao.CartDAO;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

public interface CartRepository {
    @SqlQuery("SELECT id FROM carts WHERE user_id = :userId")
    Optional<Integer> findCartIdByUserId(@Bind("userId") int userId);

    @SqlUpdate("INSERT INTO carts (user_id, created_at, updated_at) VALUES (:userId, NOW(), NOW())")
    @GetGeneratedKeys("id")
    int createCart(@Bind("userId") int userId);

    @SqlUpdate("UPDATE carts SET updated_at = NOW() WHERE id = :cartId")
    void updateCartTimestamp(@Bind("cartId") int cartId);

    @SqlQuery("SELECT c.id FROM carts c INNER JOIN users u ON c.user_id = u.id WHERE u.username = :username")
    Optional<Integer> findCartIdByUsername(@Bind("username") String username);

    @SqlQuery("""
            SELECT c.id AS cart_id, c.created_at, c.updated_at,
                   ci.id AS cart_item_id, p.id AS product_id, p.name AS product_name,
                   p.img AS product_image, v.id AS variant_id, v.name AS variant_name,
                   v.price AS product_price, ci.quantity
            FROM carts c
            LEFT JOIN cart_items ci ON c.id = ci.cart_id
            LEFT JOIN products p ON ci.product_id = p.id
            LEFT JOIN variants v ON ci.variant_id = v.id
            WHERE c.user_id = :userId
            """)
    @RegisterRowMapper(CartDAO.CartRowMapper.class)
    List<CartDAO.CartRow> getCartRowsByUserId(@Bind("userId") int userId);
}
