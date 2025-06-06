package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import newdao.CartDAO;
import newdao.OrderDAO;
import newdao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import newmodel.Cart;
import newmodel.CartItem;
import newmodel.Order;
import newmodel.Shipping;
import util.GHNOrderService;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CheckoutServlet.class.getName());
    private final OrderDAO orderDAO = new OrderDAO();
    private final CartDAO cartDAO = new CartDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession(false);

        // Ensure the user is logged in
        if (session.getAttribute("username") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Cart cart = (Cart) session.getAttribute("cart");

        if (cart == null || cart.getItems().isEmpty()) {
            request.setAttribute("message", "Your cart is empty.");
            request.getRequestDispatcher("cart.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // Retrieve user ID and cart
        String username = (String) session.getAttribute("username");
        int userId = orderDAO.getUserIdByUsername(username);
        Cart cart = (Cart) session.getAttribute("cart");
        List<CartItem> cartItems = cart.getItems();
        BigDecimal totalAmount = cart.getSubtotal().add((BigDecimal) session.getAttribute("shippingCost"));

        // Get the selected payment method
        String paymentMethod = req.getParameter("payment");

        if ("vnpay".equals(paymentMethod)) {
            // For VnPay payments, store order information in session for later use
            // after payment is successful
            session.setAttribute("pendingUserId", userId);
            session.setAttribute("pendingCartItems", cartItems);
            session.setAttribute("pendingTotalAmount", totalAmount);

            // Generate a temporary reference for the transaction
            String tempOrderRef = "TEMP_" + System.currentTimeMillis();
            session.setAttribute("tempOrderRef", tempOrderRef);

            // Redirect to VnPay payment servlet
            resp.sendRedirect(req.getContextPath() + "/vnpay-payment?orderRef=" + tempOrderRef + "&amount=" + totalAmount.multiply(new BigDecimal(100)).intValue());
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
                    String recipientName = req.getParameter("name");
                    String recipientPhone = req.getParameter("phone");
                    String recipientAddress = req.getParameter("address");

                    // Get user profile to use first_name and last_name
                    newmodel.UserProfile userProfile = userDAO.getUserProfileByUserId(userId);

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

                    // Get ward code and district ID from request (these would be selected by the user in the checkout form)
                    // For demonstration purposes, using default values
                    String wardCode = req.getParameter("wardCode");
                    String districtIdStr = req.getParameter("districtId");

                    // Default values if not provided
                    if (wardCode == null || wardCode.isEmpty()) {
                        wardCode = "1A0213"; // Example ward code
                    }

                    int districtId = 1463; // Example district ID (Ho Chi Minh City, District 1)
                    if (districtIdStr != null && !districtIdStr.isEmpty()) {
                        try {
                            districtId = Integer.parseInt(districtIdStr);
                        } catch (NumberFormatException e) {
                            LOGGER.log(Level.WARNING, "Invalid district ID format: " + districtIdStr, e);
                        }
                    }

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
                    req.setAttribute("errorCode", e.getStatusCode());
                    req.setAttribute("errorMessage", "Error communicating with shipping service: " + e.getMessage());
                    req.setAttribute("code", e.getCode());
                    req.setAttribute("codeMessage", e.getCodeMessage());
                    req.setAttribute("apiMessage", e.getApiMessage());

                    // Forward to error page
                    req.getRequestDispatcher("error.jsp").forward(req, resp);
                    return;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error creating GHN order", e);
                }

                // Clear cart
                cartDAO.clearCart(cart.getId());

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
                resp.sendRedirect(req.getContextPath() + "/payment-success.jsp");
            } else {
                req.setAttribute("errorMessage", "Failed to process your order. Please try again.");
                req.getRequestDispatcher("checkout.jsp").forward(req, resp);
            }
        }
    }
}
