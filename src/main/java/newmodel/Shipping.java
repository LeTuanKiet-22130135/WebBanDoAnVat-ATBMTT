package newmodel;

/**
 * Model class for shipping information
 * Represents a record in the shipping table
 */
public class Shipping {
    private int id;
    private int orderId;
    private int status;
    private int paymentStatus;

    // Constructors
    public Shipping() {
    }

    public Shipping(int id, int orderId, int status, int paymentStatus) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(int paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Shipping{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", status=" + status +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}