package newmodel;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Represents a shopping cart in the system
 */
public class Cart {
    private int id;
    private int userId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<CartItem> items;

    // Constructors
    public Cart() {
    }

    public Cart(int id, int userId, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    /**
     * Calculate the subtotal of all items in the cart
     * @return The subtotal as a BigDecimal
     */
    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        if (items != null) {
            for (CartItem item : items) {
                subtotal = subtotal.add(item.getTotalPrice());
            }
        }
        return subtotal;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", items=" + items +
                '}';
    }
}