package util;

import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import newmodel.Cart;
import newmodel.Order;
import newmodel.OrderDetail;

/**
 * Utility class for signature verification and related operations
 */
public class SignatureUtil {

    /**
     * Verify a digital signature
     * 
     * @param data The original data that was signed (hash)
     * @param signature The signature to verify
     * @param publicKeyBytes The public key bytes
     * @return true if the signature is valid, false otherwise
     */
    public static boolean verifySignature(byte[] data, byte[] signature, byte[] publicKeyBytes) {
        try {
            // Create a key factory and public key spec
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Create a signature instance and initialize with the public key
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);

            // Update with the data and verify the signature
            sig.update(data);
            return sig.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Generates a string representation of the cart for hashing
     * 
     * @param cart The cart to generate string for
     * @param username The username of the cart owner
     * @param paymentMethod The selected payment method
     * @param shippingCost The shipping cost
     * @return A string containing cart details
     */
    public static String generateCartInfoString(Cart cart, String username, String paymentMethod, BigDecimal shippingCost) {
        StringBuilder sb = new StringBuilder();
        sb.append("Username:").append(username).append(";");
        sb.append("PaymentMethod:").append(paymentMethod).append(";");
        sb.append("Subtotal:").append(cart.getSubtotal()).append(";");
        sb.append("ShippingCost:").append(shippingCost).append(";");
        sb.append("Total:").append(cart.getSubtotal().add(shippingCost)).append(";");

        // Add cart items
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            sb.append("CartItems:[");
            cart.getItems().forEach(item -> {
                sb.append("{");
                sb.append("ProductName:").append(item.getProductName()).append(",");
                sb.append("VariantId:").append(item.getVariantId()).append(",");
                sb.append("Quantity:").append(item.getQuantity()).append(",");
                sb.append("Price:").append(item.getPrice());
                sb.append("}");
            });
            sb.append("]");
        }

        return sb.toString();
    }

    /**
     * Generates a string representation of the order for hashing
     * 
     * @param order The order to generate string for
     * @return A string containing order details
     */
    public static String generateOrderInfoString(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("OrderID:").append(order.getId()).append(";");
        sb.append("UserID:").append(order.getUserId()).append(";");
        sb.append("OrderDate:").append(order.getOrderDate()).append(";");
        sb.append("Total:").append(order.getTotal()).append(";");
        sb.append("Payment:").append(order.getPayment()).append(";");

        // Add order details
        List<OrderDetail> details = order.getOrderDetails();
        if (details != null && !details.isEmpty()) {
            sb.append("OrderDetails:[");
            for (OrderDetail detail : details) {
                sb.append("{");
                sb.append("ProductName:").append(detail.getProductName()).append(",");
                sb.append("VariantName:").append(detail.getVariantName()).append(",");
                sb.append("Quantity:").append(detail.getQuantity()).append(",");
                sb.append("Price:").append(detail.getPrice());
                sb.append("}");
            }
            sb.append("]");
        }

        return sb.toString();
    }
}
