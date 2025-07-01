package controller;

import java.io.IOException;
import java.math.BigDecimal;
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
import util.SignatureUtil;
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

        // Get pending order ID from session
        Integer pendingOrderId = (Integer) session.getAttribute("pendingOrderId");
        if (pendingOrderId == null) {
            request.setAttribute("errorMessage", "No pending order found. Please try again.");
            request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
            return;
        }

        // Get the order
        Order pendingOrder = orderDAO.getOrderById(pendingOrderId);
        if (pendingOrder == null) {
            request.setAttribute("errorMessage", "Order not found. Please try again.");
            request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
            return;
        }

        // Verify that the order belongs to the logged-in user
        if (pendingOrder.getUserId() != userId) {
            request.setAttribute("errorMessage", "Access denied. This order does not belong to you.");
            request.getRequestDispatcher("ordervalidation.jsp").forward(request, response);
            return;
        }

        // Get cart from session (still needed for some operations)
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

            // Update the order with payment method
            orderDAO.updateOrderPayment(pendingOrderId, paymentMethod);

            // Reload the order to get the updated payment method
            pendingOrder = orderDAO.getOrderById(pendingOrderId);

            // Generate order information string to hash
            String orderInfo = SignatureUtil.generateOrderInfoString(pendingOrder);

            // Generate SHA-1 hash
            String hash = VnPayUtil.sha1(orderInfo);

            // Verify signature
            boolean verified = SignatureUtil.verifySignature(hash.getBytes(), signatureBytes, pubkey.getPubkey());

            if (verified) {
                // Set the order as verified
                orderDAO.updateOrderVerification(pendingOrderId, true);

                // Store the signature data
                orderDAO.updateOrderSignature(pendingOrderId, signatureBytes);

                // Store the pubkey ID used for verification
                orderDAO.updateOrderPubkeyId(pendingOrderId, pubkey.getId());

                // Store payment method in session
                session.setAttribute("paymentMethod", paymentMethod);

                // Set verification flag in session
                session.setAttribute("signatureVerified", true);

                // Get cart items and total amount
                List<CartItem> cartItems = cart.getItems();
                BigDecimal totalAmount = cart.getSubtotal().add(shippingCost);

                // Process payment based on payment method
                if ("vnpay".equals(paymentMethod)) {
                    // For VnPay payments, store order ID in session for later use
                    session.setAttribute("pendingOrderId", pendingOrderId);

                    // Generate a temporary reference for the transaction
                    String tempOrderRef = "TEMP_" + System.currentTimeMillis();
                    session.setAttribute("tempOrderRef", tempOrderRef);

                    // Redirect directly to VnPay payment servlet
                    response.sendRedirect(request.getContextPath() + "/vnpay-payment?orderRef=" + tempOrderRef + 
                                         "&amount=" + pendingOrder.getTotal().multiply(new BigDecimal(100)).intValue());
                } else if ("cod".equals(paymentMethod)) {
                    // For COD payment, use the existing order
                    int orderId = pendingOrderId;

                    if (orderId > 0) {
                        // Order is already verified and signature is already stored

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

}
