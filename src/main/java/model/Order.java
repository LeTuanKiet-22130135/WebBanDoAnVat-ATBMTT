package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
	private int id;
	private int userId;
	private BigDecimal totalAmount;
	private LocalDateTime orderDate;  // Renamed field
	private List<OrderItem> items;

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

	public BigDecimal getTotalAmount() {
		return totalAmount; 
	}
	public void setTotalAmount(BigDecimal totalAmount) { 
		this.totalAmount = totalAmount; 
	}

	public LocalDateTime getOrderDate() {
		return orderDate; 
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate; 
	}

	public List<OrderItem> getItems() {
		return items; 
	}
	public void setItems(List<OrderItem> items) {
		this.items = items; 
	}
}
