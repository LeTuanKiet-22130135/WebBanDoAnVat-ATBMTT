package repository.shipping;

import newdao.OrderDAO;
import newmodel.Shipping;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.Optional;

public interface ShippingRepository {
    @SqlUpdate("INSERT INTO shipping (oId, status, paymentStat) VALUES (:orderId, :status, :paymentStatus)")
    @GetGeneratedKeys("id")
    int addShipping(@Bind("orderId") int orderId,
                    @Bind("status") int status,
                    @Bind("paymentStatus") int paymentStatus);

    @SqlUpdate("UPDATE shipping SET paymentStat = :paymentStatus WHERE oId = :orderId")
    boolean updatePaymentStatus(@Bind("orderId") int orderId,
                                @Bind("paymentStatus") int paymentStatus);

    @SqlQuery("SELECT * FROM shipping WHERE oId = :orderId")
    @RegisterRowMapper(OrderDAO.ShippingMapper.class)
    Optional<Shipping> getShippingByOrderId(@Bind("orderId") int orderId);
}
