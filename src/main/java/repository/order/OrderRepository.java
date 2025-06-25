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

    @SqlUpdate("INSERT INTO orders (uId, orderDate, total) VALUES (:userId, CURDATE(), :total)")
    @GetGeneratedKeys("id")
    int createOrder(@Bind("userId") int userId, @Bind("total") BigDecimal total);

    @SqlQuery("SELECT * FROM orders WHERE id = :orderId")
    @RegisterRowMapper(OrderDAO.OrderMapper.class)
    Optional<Order> getOrderById(@Bind("orderId") int orderId);
}
