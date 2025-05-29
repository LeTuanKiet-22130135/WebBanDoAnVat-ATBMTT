package model;

import java.math.BigDecimal;

public abstract class Item {
	
	protected int id;
	protected int productId;      // Added field for product ID
	protected String productName; // Product name fetched via join
	protected int quantity;
	protected BigDecimal price;

	// Getters and Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id; 
	}

	public int getProductId() {
		return productId; 
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName; 
	}

	public void setProductName(String productName) {
		this.productName = productName; 
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

	// Method to calculate total price for this item
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }
}
