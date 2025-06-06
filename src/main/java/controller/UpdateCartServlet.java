package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import newdao.CartDAO;
import newdao.UserDAO;
import newmodel.Cart;
import newmodel.CartItem;
import newmodel.User;

@WebServlet("/UpdateCartServlet")
public class UpdateCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CartDAO cartDAO = new CartDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get session and check if user is logged in
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, false, "User not logged in");
            } else {
                response.sendRedirect("login");
            }
            return;
        }

        String productIdParam = request.getParameter("productId");
        String action = request.getParameter("action");
        String itemIndexParam = request.getParameter("itemIndex");

        // Debug: Log parameters received
        System.out.println("UpdateCartServlet received parameters: productId=" + productIdParam + 
                           ", action=" + action + ", itemIndex=" + itemIndexParam);

        // If productId is not in the request, try to get it from the session
        if (productIdParam == null || productIdParam.isEmpty()) {
            // Get the cart from the session
            Cart sessionCart = (Cart) session.getAttribute("cart");
            if (sessionCart == null || sessionCart.getItems() == null || sessionCart.getItems().isEmpty()) {
                if (isAjaxRequest(request)) {
                    sendJsonResponse(response, false, "Product ID is required and cart is empty in session");
                } else {
                    response.sendRedirect("cart");
                }
                return;
            }

            // Get the item index from the request (if available)
            itemIndexParam = request.getParameter("itemIndex");
            int itemIndex = -1;
            if (itemIndexParam != null && !itemIndexParam.isEmpty()) {
                try {
                    itemIndex = Integer.parseInt(itemIndexParam);
                } catch (NumberFormatException e) {
                    // Invalid index, will try other methods
                }
            }

            // Try to find the product ID based on the item index or other identifiers
            if (itemIndex >= 0 && itemIndex < sessionCart.getItems().size()) {
                // Use the item at the specified index
                productIdParam = String.valueOf(sessionCart.getItems().get(itemIndex).getProductId());
            } else {
                // No valid item index, can't determine which product to update
                if (isAjaxRequest(request)) {
                    sendJsonResponse(response, false, "Cannot determine which product to update");
                } else {
                    response.sendRedirect("cart");
                }
                return;
            }
        }

        int productId = Integer.parseInt(productIdParam);

        // Get variant ID from request, default to 0 if not provided
        int variantId = 0;
        String variantIdParam = request.getParameter("variantId");
        if (variantIdParam != null && !variantIdParam.isEmpty()) {
            variantId = Integer.parseInt(variantIdParam);
        }

        try {
            // Get user by username
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                if (isAjaxRequest(request)) {
                    sendJsonResponse(response, false, "User not found");
                } else {
                    response.sendRedirect("cart");
                }
                return;
            }

            // Fetch or validate the cart ID using CartDAO
            int cartId = cartDAO.getCartIdByUsername(username);
            if (cartId == -1) {
                if (isAjaxRequest(request)) {
                    sendJsonResponse(response, false, "Cart not found");
                } else {
                    response.sendRedirect("cart");
                }
                return;
            }

            // Update the cart item quantity based on the action
            if ("increase".equals(action)) {
                cartDAO.updateCartItemQuantity(cartId, productId, variantId, 1);
            } else if ("decrease".equals(action)) {
                cartDAO.updateCartItemQuantity(cartId, productId, variantId, -1);
            }

            // Refresh cart data from database and update session
            Cart cart = cartDAO.getCartByUserId(user.getId());
            if (cart != null) {
                session.setAttribute("cart", cart);
                session.setAttribute("cartSubtotal", cart.getSubtotal());

                // Calculate total items count and store in session
                int totalItems = 0;
                if (cart.getItems() != null) {
                    for (CartItem item : cart.getItems()) {
                        totalItems += item.getQuantity();
                    }
                }
                session.setAttribute("cartItemCount", totalItems);

                if (isAjaxRequest(request)) {
                    // Send JSON response with updated cart data
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print("{\"success\":true,\"cartSubtotal\":\"" + cart.getSubtotal() + "\",\"cartItemCount\":" + totalItems + "}");
                    out.flush();
                    return;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, false, "Error updating cart item quantity");
            } else {
                throw new ServletException("Error updating cart item quantity.");
            }
            return;
        }

        // Only redirect if not an AJAX request
        if (!isAjaxRequest(request)) {
            response.sendRedirect("cart");
        }
    }

    // Helper method to check if request is an AJAX request
    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    // Helper method to send JSON response
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"success\":" + success + ",\"message\":\"" + message + "\"}");
        out.flush();
    }
}
