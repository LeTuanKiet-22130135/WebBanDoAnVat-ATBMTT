package controller;

import util.VnPayUtil;
import util.GHNOrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import newmodel.Order;
import newmodel.Shipping;
import newmodel.UserProfile;
import newdao.UserDAO;

@WebServlet(name = "VnPayReturnServlet", urlPatterns = {"/vnpay_return"})
public class VnPayReturnServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(VnPayReturnServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, String> vnp_Params = new HashMap<>();

        // Get all parameters from the request
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                vnp_Params.put(paramName, paramValue);
            }
        }

        // Remove vnp_SecureHash from the map to verify the checksum
        if (vnp_Params.containsKey("vnp_SecureHash")) {
            String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
            vnp_Params.remove("vnp_SecureHash");

            // Remove vnp_SecureHashType if present
            if (vnp_Params.containsKey("vnp_SecureHashType")) {
                vnp_Params.remove("vnp_SecureHashType");
            }

            // Verify the checksum
            String signValue = VnPayUtil.hashAllFields(vnp_Params);
            if (signValue.equals(vnp_SecureHash)) {
                // Valid signature
                String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");
                if ("00".equals(vnp_ResponseCode)) {
                    // Payment successful
                    String vnp_TxnRef = vnp_Params.get("vnp_TxnRef");
                    String vnp_Amount = vnp_Params.get("vnp_Amount");
                    String vnp_OrderInfo = vnp_Params.get("vnp_OrderInfo");
                    String vnp_BankCode = vnp_Params.get("vnp_BankCode");
                    String vnp_PayDate = vnp_Params.get("vnp_PayDate");

                    // Get session and required DAOs
                    HttpSession session = request.getSession();
                    newdao.OrderDAO orderDAO = new newdao.OrderDAO();
                    newdao.CartDAO cartDAO = new newdao.CartDAO();

                    // Get pending order information from session
                    Integer pendingUserId = (Integer) session.getAttribute("pendingUserId");
                    @SuppressWarnings("unchecked")
                    List<newmodel.CartItem> pendingCartItems = (List<newmodel.CartItem>) session.getAttribute("pendingCartItems");
                    BigDecimal pendingTotalAmount = (BigDecimal) session.getAttribute("pendingTotalAmount");
                    newmodel.Cart cart = (newmodel.Cart) session.getAttribute("cart");

                    if (pendingUserId != null && pendingCartItems != null && pendingTotalAmount != null) {
                        // Create the order now that payment is successful
                        int orderId = orderDAO.createOrder(pendingUserId.intValue(), pendingTotalAmount, pendingCartItems);

                        if (orderId > 0) {
                            // Add shipping information with payment status = 1 (paid)
                            int shippingId = orderDAO.addShipping(orderId, 0, 1); // 0 = placed, 1 = paid

                            // Variable to store the GHN order code
                            String ghnOrderCode = null;

                            try {
                                // Get recipient information from request parameters or use default values
                                // In a real application, these would be collected during checkout
                                String recipientName = request.getParameter("name");
                                String recipientPhone = request.getParameter("phone");
                                String recipientAddress = request.getParameter("address");

                                // Get user profile to use first_name and last_name
                                UserProfile userProfile = userDAO.getUserProfileByUserId(pendingUserId);

                                // Use user profile name if available, otherwise use request parameter or default
                                if (recipientName == null || recipientName.isEmpty()) {
                                    if (userProfile != null && 
                                        userProfile.getFirstName() != null && !userProfile.getFirstName().isEmpty() &&
                                        userProfile.getLastName() != null && !userProfile.getLastName().isEmpty()) {
                                        recipientName = userProfile.getFirstName() + " " + userProfile.getLastName();
                                    } else {
                                        recipientName = "TinTest124"; // Default name from issue description
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
                                        recipientAddress = "72 Thành Thái, Phường 14, Quận 10, Hồ Chí Minh, Vietnam"; // Default address from issue description
                                    }
                                }

                                // Default ward code and district ID
                                String wardCode = "1A0213"; // Example ward code
                                int districtId = 1463; // Example district ID (Ho Chi Minh City, District 1)

                                // Get order and shipping objects
                                Order order = orderDAO.getOrderById(orderId);
                                Shipping shipping = orderDAO.getShippingByOrderId(orderId);

                                if (order != null) {
                                    // Create GHN order
                                    ghnOrderCode = GHNOrderService.createGHNOrder(
                                            order, 
                                            shipping, 
                                            recipientName, 
                                            recipientPhone, 
                                            recipientAddress, 
                                            wardCode, 
                                            districtId);

                                    if (ghnOrderCode != null) {
                                        LOGGER.info("GHN order created successfully with code: " + ghnOrderCode);
                                        // Store the GHN order code in session for display on the success page
                                        session.setAttribute("ghnOrderCode", ghnOrderCode);
                                    } else {
                                        LOGGER.warning("Failed to create GHN order for order ID: " + orderId);
                                    }
                                } else {
                                    LOGGER.warning("Could not retrieve order with ID: " + orderId);
                                }
                            } catch (util.GHNApiException e) {
                                LOGGER.log(Level.SEVERE, "GHN API error: " + e.getMessage() + ", Status code: " + e.getStatusCode() + 
                                        ", Code: " + e.getCode() + ", Code Message: " + e.getCodeMessage(), e);

                                // Set error attributes
                                request.setAttribute("errorCode", e.getStatusCode());
                                request.setAttribute("errorMessage", "Error communicating with shipping service: " + e.getMessage());
                                request.setAttribute("code", e.getCode());
                                request.setAttribute("codeMessage", e.getCodeMessage());
                                request.setAttribute("apiMessage", e.getApiMessage());

                                // Forward to error page
                                request.getRequestDispatcher("error.jsp").forward(request, response);
                                return;
                            } catch (Exception e) {
                                LOGGER.log(Level.SEVERE, "Error creating GHN order", e);
                            }

                            // Clear cart
                            if (cart != null) {
                                cartDAO.clearCart(cart.getId());
                            }

                            // Clear pending order information from session
                            session.removeAttribute("pendingUserId");
                            session.removeAttribute("pendingCartItems");
                            session.removeAttribute("pendingTotalAmount");
                            session.removeAttribute("tempOrderRef");

                            // Store payment information in session
                            session.setAttribute("vnp_TxnRef", vnp_TxnRef);
                            session.setAttribute("vnp_Amount", vnp_Amount);
                            session.setAttribute("vnp_OrderInfo", vnp_OrderInfo);
                            session.setAttribute("vnp_BankCode", vnp_BankCode);
                            session.setAttribute("vnp_PayDate", vnp_PayDate);
                            session.setAttribute("paymentStatus", "success");

                            // Redirect to success page
                            response.sendRedirect(request.getContextPath() + "/payment-success.jsp");
                        } else {
                            // Failed to create order
                            session.setAttribute("paymentStatus", "failed");
                            session.setAttribute("errorMessage", "Payment was successful but order creation failed");

                            // Redirect to failure page
                            response.sendRedirect(request.getContextPath() + "/payment-failed.jsp");
                        }
                    } else {
                        // Missing order information
                        session.setAttribute("paymentStatus", "failed");
                        session.setAttribute("errorMessage", "Payment was successful but order information was missing");

                        // Redirect to failure page
                        response.sendRedirect(request.getContextPath() + "/payment-failed.jsp");
                    }
                } else {
                    // Payment failed
                    HttpSession session = request.getSession();
                    session.setAttribute("paymentStatus", "failed");
                    session.setAttribute("vnp_ResponseCode", vnp_ResponseCode);

                    // Redirect to failure page
                    response.sendRedirect(request.getContextPath() + "/payment-failed.jsp");
                }
            } else {
                // Invalid signature
                HttpSession session = request.getSession();
                session.setAttribute("paymentStatus", "invalid");

                // Redirect to failure page
                response.sendRedirect(request.getContextPath() + "/payment-failed.jsp");
            }
        } else {
            // No secure hash found
            HttpSession session = request.getSession();
            session.setAttribute("paymentStatus", "invalid");

            // Redirect to failure page
            response.sendRedirect(request.getContextPath() + "/payment-failed.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
