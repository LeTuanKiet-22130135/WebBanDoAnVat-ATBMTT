package newdao;

import newmodel.Order;
import newmodel.OrderDetail;
import newmodel.Shipping;
import newmodel.CartItem;
import repository.order.OrderDetailRepository;
import repository.order.OrderRepository;
import repository.shipping.ShippingRepository;
import repository.user.UserRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OrderDAO {
    private final OrderRepository orderRepo;
    private final OrderDetailRepository orderDetailRepo;
    private final ShippingRepository shippingRepo;
    private final UserRepository userRepo;

    public OrderDAO() {
        Jdbi jdbi = JdbiConfig.getJdbi();
        this.orderRepo = jdbi.onDemand(OrderRepository.class);
        this.orderDetailRepo = jdbi.onDemand(OrderDetailRepository.class);
        this.shippingRepo = jdbi.onDemand(ShippingRepository.class);
        this.userRepo = jdbi.onDemand(UserRepository.class);
    }

    // Row Mappers
    public static class OrderMapper implements RowMapper<Order> {
        @Override
        public Order map(ResultSet rs, StatementContext ctx) throws SQLException {
            Order order = new Order();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("uId"));
            order.setOrderDate(rs.getDate("orderDate"));
            order.setTotal(rs.getBigDecimal("total"));
            return order;
        }
    }

    public static class OrderDetailMapper implements RowMapper<OrderDetail> {
        @Override
        public OrderDetail map(ResultSet rs, StatementContext ctx) throws SQLException {
            OrderDetail detail = new OrderDetail();
            detail.setId(rs.getInt("id"));
            detail.setOrderId(rs.getInt("oId"));
            detail.setVariantId(rs.getInt("vId"));
            detail.setQuantity(rs.getInt("quantity"));
            detail.setPrice(rs.getBigDecimal("price"));
            detail.setVariantName(rs.getString("variant_name"));
            detail.setProductName(rs.getString("product_name"));
            return detail;
        }
    }

    public static class ShippingMapper implements RowMapper<Shipping> {
        @Override
        public Shipping map(ResultSet rs, StatementContext ctx) throws SQLException {
            Shipping shipping = new Shipping();
            shipping.setId(rs.getInt("id"));
            shipping.setOrderId(rs.getInt("oId"));
            shipping.setStatus(rs.getInt("status"));
            shipping.setPaymentStatus(rs.getInt("paymentStat"));
            return shipping;
        }
    }

    // Refactored DAO methods
    public int getUserIdByUsername(String username) {
        return userRepo.findUserIdByUsername(username).orElse(-1);
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = orderRepo.getOrdersByUserId(userId);
        orders.forEach(order ->
                order.setOrderDetails(orderDetailRepo.getOrderDetailsByOrderId(order.getId()))
        );
        return orders;
    }

    @Transaction
    public int createOrder(int userId, BigDecimal totalAmount, List<CartItem> cartItems) {
        int orderId = orderRepo.createOrder(userId, totalAmount);
        orderDetailRepo.insertOrderDetails(orderId, cartItems);
        return orderId;
    }

    public int addShipping(int orderId, int status, int paymentStatus) {
        return shippingRepo.addShipping(orderId, status, paymentStatus);
    }

    public boolean updatePaymentStatus(int orderId, int paymentStatus) {
        return shippingRepo.updatePaymentStatus(orderId, paymentStatus);
    }

    public Order getOrderById(int orderId) {
        return orderRepo.getOrderById(orderId)
                .map(order -> {
                    order.setOrderDetails(orderDetailRepo.getOrderDetailsByOrderId(orderId));
                    return order;
                })
                .orElse(null);
    }

    public Shipping getShippingByOrderId(int orderId) {
        return shippingRepo.getShippingByOrderId(orderId).orElse(null);
    }
}