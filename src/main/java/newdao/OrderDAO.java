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
            // Handle the verify column, which might be null for existing orders
            try {
                order.setVerify(rs.getBoolean("verify"));
            } catch (SQLException e) {
                // If the column doesn't exist or is null, set verify to false
                order.setVerify(false);
            }
            // Handle the signature column, which might be null for existing orders
            try {
                order.setSignature(rs.getBytes("signature"));
            } catch (SQLException e) {
                // If the column doesn't exist or is null, set signature to null
                order.setSignature(null);
            }
            // Handle the payment column, which might be null for existing orders
            try {
                order.setPayment(rs.getString("payment"));
            } catch (SQLException e) {
                // If the column doesn't exist or is null, set payment to null
                order.setPayment(null);
            }
            // Handle the pubkey_id column, which might be null for existing orders
            try {
                int pubkeyId = rs.getInt("pubkey_id");
                if (!rs.wasNull()) {
                    order.setPubkeyId(pubkeyId);
                }
            } catch (SQLException e) {
                // If the column doesn't exist or is null, set pubkeyId to null
                order.setPubkeyId(null);
            }
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
        return createOrder(userId, totalAmount, cartItems, null);
    }

    @Transaction
    public int createOrder(int userId, BigDecimal totalAmount, List<CartItem> cartItems, Integer pubkeyId) {
        int orderId = orderRepo.createOrder(userId, totalAmount, pubkeyId);
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

    /**
     * Updates the verification status of an order
     * 
     * @param orderId The ID of the order to update
     * @param verify The new verification status
     * @return true if the update was successful, false otherwise
     */
    public boolean updateOrderVerification(int orderId, boolean verify) {
        return orderRepo.updateOrderVerification(orderId, verify);
    }

    /**
     * Updates the signature of an order
     * 
     * @param orderId The ID of the order to update
     * @param signature The new signature
     * @return true if the update was successful, false otherwise
     */
    public boolean updateOrderSignature(int orderId, byte[] signature) {
        return orderRepo.updateOrderSignature(orderId, signature);
    }

    /**
     * Updates the payment type of an order
     * 
     * @param orderId The ID of the order to update
     * @param payment The new payment type
     * @return true if the update was successful, false otherwise
     */
    public boolean updateOrderPayment(int orderId, String payment) {
        return orderRepo.updateOrderPayment(orderId, payment);
    }

    /**
     * Updates the pubkey ID of an order
     * 
     * @param orderId The ID of the order to update
     * @param pubkeyId The ID of the pubkey used to verify the order
     * @return true if the update was successful, false otherwise
     */
    public boolean updateOrderPubkeyId(int orderId, int pubkeyId) {
        return orderRepo.updateOrderPubkeyId(orderId, pubkeyId);
    }
}
