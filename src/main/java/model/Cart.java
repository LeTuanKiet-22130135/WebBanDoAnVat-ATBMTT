package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Cart {

	private int id; // Unique ID for the cart
	private int userId; // ID of the user who owns the cart
	private Timestamp createdAt; // Timestamp when the cart was created
	private Timestamp updatedAt; // Timestamp when the cart was last updated
	private List<CartItem> items; // List of items in the cart
	
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
	
	// New method to calculate the subtotal
    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            subtotal = subtotal.add(item.getTotalPrice());
        }
        return subtotal;
    }
	
}
