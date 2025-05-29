package newmodel;

import java.math.BigDecimal;

/**
 * Model class for order details
 * Represents a record in the orderdetails table
 */
public class OrderDetail {
    private int id;
    private int orderId;
    private int variantId;
    private int quantity;
    private BigDecimal price;
    private String variantName;
    private String productName;

    // Constructors
    public OrderDetail() {
    }

    public OrderDetail(int id, int orderId, int variantId, int quantity, BigDecimal price) {
        this.id = id;
        this.orderId = orderId;
        this.variantId = variantId;
        this.quantity = quantity;
        this.price = price;
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

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Calculate the total price for this order detail (price * quantity)
     * @return The total price as a BigDecimal
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", variantId=" + variantId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", variantName='" + variantName + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}
