package model;

import java.math.BigDecimal;

public class CartItem extends Item{
	
    private int cartId; // ID of the cart this item belongs to
    private String productImageUrl; // Image URL for the product
    private BigDecimal productPrice; // Price of the product
    	
	public int getCartId() {
		return cartId;
	}
	public void setCartId(int cartId) {
		this.cartId = cartId;
	}
	
	public String getProductImageUrl() {
		return productImageUrl;
	}
	public void setProductImageUrl(String productImageUrl) {
		this.productImageUrl = productImageUrl;
	}
	public BigDecimal getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(BigDecimal productPrice) {
		this.productPrice = productPrice;
	}
    
	@Override
    public BigDecimal getTotalPrice() {
        return productPrice.multiply(new BigDecimal(quantity));
    }
}
