package newmodel;

import java.math.BigDecimal;

/**
 * Represents an item in a shopping cart
 */
public class CartItem {
    private int id;
    private int cartId;
    private int productId;
    private int variantId; // New field for product variant
    private String productName;
    private String productImageUrl;
    private int quantity;
    private BigDecimal price;

    // Constructors
    public CartItem() {
    }

    public CartItem(int id, int cartId, int productId, int variantId, String productName, 
                   String productImageUrl, int quantity, BigDecimal price) {
        this.id = id;
        this.cartId = cartId;
        this.productId = productId;
        this.variantId = variantId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
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

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
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

    /**
     * Calculate the total price for this item (price * quantity)
     * @return The total price as a BigDecimal
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", productId=" + productId +
                ", variantId=" + variantId +
                ", productName='" + productName + '\'' +
                ", productImageUrl='" + productImageUrl + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}