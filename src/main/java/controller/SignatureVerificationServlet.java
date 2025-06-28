package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import newdao.CartDAO;
import newdao.OrderDAO;
import newdao.PubkeyDAO;
import newdao.UserDAO;
import newmodel.Cart;
import newmodel.CartItem;
import newmodel.Order;
import newmodel.Pubkey;
import newmodel.Shipping;
import util.GHNOrderService;
import util.VnPayUtil;

/**
 * Servlet for verifying digital signatures of order hashes
 */
@WebServlet("/verifysignature")
@MultipartConfig
public class SignatureVerificationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final OrderDAO orderDAO = new OrderDAO();
    private final UserDAO userDAO = new UserDAO();
    private final PubkeyDAO pubkeyDAO = new PubkeyDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get user ID
        int userId = userDAO.getUserIdByUsername(username);

        // Check if user has a public key
        Pubkey pubkey = pubkeyDAO.getPubkeyByUserId(userId);
        if (pubkey == null || !pubkey.isAvailable()) {
            request.setAttribute("errorMessage", "You don't have an active public key. Please register one in your profile.");
            request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
            return;
        }

        // Get payment method from request or session
        String paymentMethod = request.getParameter("paymentMethod");
        if (paymentMethod == null || paymentMethod.isEmpty()) {
            paymentMethod = (String) session.getAttribute("paymentMethod");
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                request.setAttribute("errorMessage", "Payment method not specified.");
                request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
                return;
            }
        }

        // Get cart from session
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.getItems().isEmpty()) {
            request.setAttribute("errorMessage", "Your cart is empty.");
            request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
            return;
        }

        // Get shipping cost from session
        BigDecimal shippingCost = (BigDecimal) session.getAttribute("shippingCost");
        if (shippingCost == null) {
            shippingCost = BigDecimal.ZERO;
        }

        // Get signature file
        Part filePart = request.getPart("signatureFile");
        if (filePart == null || filePart.getSize() == 0) {
            request.setAttribute("errorMessage", "No signature file uploaded.");
            request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
            return;
        }

        try {
            // Read signature bytes
            byte[] signatureBytes = filePart.getInputStream().readAllBytes();

            // Generate order information string to hash
            String orderInfo = generateCartInfoString(cart, username, paymentMethod, shippingCost);

            // Generate SHA-1 hash
            String hash = VnPayUtil.sha1(orderInfo);

            // Verify signature
            boolean verified = verifySignature(hash.getBytes(), signatureBytes, pubkey.getPubkey());

            if (verified) {
                // Store payment method in session
                session.setAttribute("paymentMethod", paymentMethod);

                // Get cart items and total amount
                List<CartItem> cartItems = cart.getItems();
                BigDecimal totalAmount = cart.getSubtotal().add(shippingCost);

                // Process payment based on payment method
                if ("vnpay".equals(paymentMethod)) {
                    // For VnPay payments, store order information in session for later use
                    session.setAttribute("pendingUserId", userId);
                    session.setAttribute("pendingCartItems", cartItems);
                    session.setAttribute("pendingTotalAmount", totalAmount);

                    // Generate a temporary reference for the transaction
                    String tempOrderRef = "TEMP_" + System.currentTimeMillis();
                    session.setAttribute("tempOrderRef", tempOrderRef);

                    // Redirect directly to VnPay payment servlet
                    response.sendRedirect(request.getContextPath() + "/vnpay-payment?orderRef=" + tempOrderRef + 
                                         "&amount=" + totalAmount.multiply(new BigDecimal(100)).intValue());
                } else if ("cod".equals(paymentMethod)) {
                    // For COD payment, create the order immediately
                    int orderId = orderDAO.createOrder(userId, totalAmount, cartItems);

                    if (orderId > 0) {
                        // Add shipping information
                        int shippingId = orderDAO.addShipping(orderId, 0, 1); // 0 = placed, 1 = paid (for direct check)

                        // Variable to store the GHN order code
                        String ghnOrderCode = null;

                        try {
                            // Get recipient information from request
                            String recipientName = request.getParameter("name");
                            String recipientPhone = request.getParameter("phone");
                            String recipientAddress = request.getParameter("address");

                            // Get user profile to use first_name and last_name
                            newmodel.UserProfile userProfile = userDAO.getUserProfileByUserId(userId);

                            // Use user profile name if available, otherwise use request parameter or default
                            if (recipientName == null || recipientName.isEmpty()) {
                                if (userProfile != null && 
                                    userProfile.getFirstName() != null && !userProfile.getFirstName().isEmpty() &&
                                    userProfile.getLastName() != null && !userProfile.getLastName().isEmpty()) {
                                    recipientName = userProfile.getFirstName() + " " + userProfile.getLastName();
                                } else {
                                    recipientName = "TinTest124"; // Default name
                                }
                            }
                            if (recipientPhone == null || recipientPhone.isEmpty()) {
                                recipientPhone = "0941172573"; // Default phone
                            }
                            if (recipientAddress == null || recipientAddress.isEmpty()) {
                                // Try to use address_line1 from userprofile
                                if (userProfile != null && 
                                    userProfile.getAddressLine1() != null && 
                                    !userProfile.getAddressLine1().isEmpty()) {
                                    recipientAddress = userProfile.getAddressLine1();
                                } else {
                                    recipientAddress = "72 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Vietnam"; // Default address
                                }
                            }

                            // Get ward code and district ID from request (these would be selected by the user in the checkout form)
                            // For demonstration purposes, using default values
                            String wardCode = request.getParameter("wardCode");
                            String districtIdStr = request.getParameter("districtId");

                            // Default values if not provided
                            if (wardCode == null || wardCode.isEmpty()) {
                                wardCode = "1A0213"; // Example ward code
                            }

                            int districtId = 1463; // Example district ID (Ho Chi Minh City, District 1)
                            if (districtIdStr != null && !districtIdStr.isEmpty()) {
                                try {
                                    districtId = Integer.parseInt(districtIdStr);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Get order and shipping objects
                            Order order = orderDAO.getOrderById(orderId);
                            Shipping shipping = orderDAO.getShippingByOrderId(orderId);

                            if (order != null) {
                                // Create GHN order
                                ghnOrderCode = util.GHNOrderService.createGHNOrder(
                                        order, 
                                        shipping, 
                                        recipientName, 
                                        recipientPhone, 
                                        recipientAddress, 
                                        wardCode, 
                                        districtId);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Clear cart
                        new CartDAO().clearCart(cart.getId());

                        // Set session attributes for payment-success.jsp
                        String orderRef = "COD_" + orderId;
                        session.setAttribute("vnp_TxnRef", orderRef);
                        session.setAttribute("vnp_Amount", String.valueOf(totalAmount.multiply(new BigDecimal(100)).intValue()));
                        session.setAttribute("vnp_OrderInfo", "Order #" + orderId);
                        session.setAttribute("vnp_BankCode", "COD");
                        session.setAttribute("vnp_PayDate", new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date()));
                        session.setAttribute("paymentStatus", "success");

                        // Set GHN order code if available
                        if (ghnOrderCode != null) {
                            session.setAttribute("ghnOrderCode", ghnOrderCode);
                        }

                        // Redirect to payment-success.jsp
                        response.sendRedirect(request.getContextPath() + "/payment-success.jsp");
                    } else {
                        request.setAttribute("errorMessage", "Failed to process your order. Please try again.");
                        request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
                    }
                } else {
                    // Unknown payment method
                    request.setAttribute("errorMessage", "Unknown payment method: " + paymentMethod);
                    request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("errorMessage", "Signature verification failed. Please ensure you're using the correct private key.");
                request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error verifying signature: " + e.getMessage());
            request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
        }
    }

    /**
     * Verify a digital signature
     * 
     * @param data The original data that was signed (hash)
     * @param signature The signature to verify
     * @param publicKeyBytes The public key bytes
     * @return true if the signature is valid, false otherwise
     */
    private boolean verifySignature(byte[] data, byte[] signature, byte[] publicKeyBytes) {
        try {
            // Create a key factory and public key spec
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
            java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Create a signature instance and initialize with the public key
            Signature sig = Signature.getInstance("SHA1withRSA");
            System.out.println(publicKey.getAlgorithm() + " " + publicKey.getFormat() + " " + publicKey.getEncoded().length + " " + Base64.getEncoder().encodeToString(publicKey.getEncoded()) );
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
    private String generateCartInfoString(Cart cart, String username, String paymentMethod, BigDecimal shippingCost) {
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
}
