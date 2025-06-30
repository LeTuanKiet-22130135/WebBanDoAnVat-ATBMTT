package newmodel;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Model class for orders
 * Represents a record in the orders table
 */
public class Order {
    private int id;
    private int userId;
    private Date orderDate;
    private BigDecimal total;
    private boolean verify;
    private byte[] signature;
    private String payment;
    private List<OrderDetail> orderDetails;

    // Constructors
    public Order() {
    }

    public Order(int id, int userId, Date orderDate, BigDecimal total) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.total = total;
        this.verify = false;
        this.signature = null;
        this.payment = null;
    }

    public Order(int id, int userId, Date orderDate, BigDecimal total, boolean verify) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.total = total;
        this.verify = verify;
        this.signature = null;
        this.payment = null;
    }

    public Order(int id, int userId, Date orderDate, BigDecimal total, boolean verify, byte[] signature) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.total = total;
        this.verify = verify;
        this.signature = signature;
        this.payment = null;
    }

    public Order(int id, int userId, Date orderDate, BigDecimal total, boolean verify, byte[] signature, String payment) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.total = total;
        this.verify = verify;
        this.signature = signature;
        this.payment = payment;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", total=" + total +
                ", verify=" + verify +
                ", signature=" + (signature != null ? Arrays.toString(signature) : "null") +
                ", payment='" + payment + '\'' +
                '}';
    }
}
