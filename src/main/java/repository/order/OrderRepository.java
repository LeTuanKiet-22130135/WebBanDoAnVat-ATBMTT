package repository.order;

import newdao.OrderDAO;
import newmodel.Order;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    @SqlQuery("SELECT id FROM users WHERE username = :username")
    Optional<Integer> getUserIdByUsername(@Bind("username") String username);

    @SqlQuery("SELECT * FROM orders WHERE uId = :userId ORDER BY orderDate DESC")
    @RegisterRowMapper(OrderDAO.OrderMapper.class)
    List<Order> getOrdersByUserId(@Bind("userId") int userId);

    @SqlUpdate("INSERT INTO orders (uId, orderDate, total, pubkey_id) VALUES (:userId, CURDATE(), :total, :pubkeyId)")
    @GetGeneratedKeys("id")
    int createOrder(@Bind("userId") int userId, @Bind("total") BigDecimal total, @Bind("pubkeyId") Integer pubkeyId);

    @SqlQuery("SELECT * FROM orders WHERE id = :orderId")
    @RegisterRowMapper(OrderDAO.OrderMapper.class)
    Optional<Order> getOrderById(@Bind("orderId") int orderId);

    @SqlUpdate("UPDATE orders SET verify = :verify WHERE id = :orderId")
    boolean updateOrderVerification(@Bind("orderId") int orderId, @Bind("verify") boolean verify);

    @SqlUpdate("UPDATE orders SET signature = :signature WHERE id = :orderId")
    boolean updateOrderSignature(@Bind("orderId") int orderId, @Bind("signature") byte[] signature);

    @SqlUpdate("UPDATE orders SET payment = :payment WHERE id = :orderId")
    boolean updateOrderPayment(@Bind("orderId") int orderId, @Bind("payment") String payment);

    @SqlUpdate("UPDATE orders SET pubkey_id = :pubkeyId WHERE id = :orderId")
    boolean updateOrderPubkeyId(@Bind("orderId") int orderId, @Bind("pubkeyId") int pubkeyId);
}
