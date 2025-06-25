package repository.order;

import newdao.OrderDAO;
import newmodel.CartItem;
import newmodel.OrderDetail;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface OrderDetailRepository {
    @SqlQuery("""
            SELECT od.*, v.name AS variant_name, p.name AS product_name 
            FROM orderdetails od 
            JOIN variants v ON od.vId = v.id 
            JOIN products p ON v.pId = p.id 
            WHERE od.oId = :orderId
            """)
    @RegisterRowMapper(OrderDAO.OrderDetailMapper.class)
    List<OrderDetail> getOrderDetailsByOrderId(@Bind("orderId") int orderId);

    @SqlBatch("INSERT INTO orderdetails (oId, vId, quantity, price) VALUES (:orderId, :variantId, :quantity, :price)")
    void insertOrderDetails(@Bind("orderId") int orderId,
                            @BindBean List<CartItem> items);
}
