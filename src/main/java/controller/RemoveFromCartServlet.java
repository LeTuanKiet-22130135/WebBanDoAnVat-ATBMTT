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

@WebServlet("/RemoveFromCartServlet")
public class RemoveFromCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CartDAO cartDAO = new CartDAO();
    private final UserDAO userDAO = new UserDAO();

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
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
        String variantIdParam = request.getParameter("variantId");

        if (productIdParam == null || productIdParam.isEmpty()) {
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, false, "Product ID is required");
            } else {
                response.sendRedirect("cart");
            }
            return;
        }

        try {
            final int productId = Integer.parseInt(productIdParam);
            // Get variant ID from request, default to 0 if not provided
            final int variantId;
            if (variantIdParam != null && !variantIdParam.isEmpty()) {
                variantId = Integer.parseInt(variantIdParam);
            } else {
                variantId = 0;
            }

            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                if (isAjaxRequest(request)) {
                    sendJsonResponse(response, false, "User not found");
                } else {
                    response.sendRedirect("cart");
                }
                return;
            }

            int cartId = cartDAO.getCartIdByUsername(username);
            if (cartId == -1) {
                if (isAjaxRequest(request)) {
                    sendJsonResponse(response, false, "Cart not found");
                } else {
                    response.sendRedirect("cart");
                }
                return;
            }

            // Remove the item from the cart using CartDAO
            cartDAO.removeItemFromCart(cartId, productId, variantId);

            // Update the session cart (if present)
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart != null && cart.getItems() != null) {
                // Recalculate cart subtotal after removing item
                cart.getItems().removeIf(item -> 
                    item.getProductId() == productId && item.getVariantId() == variantId);
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
            }

            if (isAjaxRequest(request)) {
                // Send JSON response with updated cart data
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                PrintWriter out = response.getWriter();

                // Get the total items count from the session
                int totalItems = 0;
                if (session.getAttribute("cartItemCount") != null) {
                    totalItems = (int) session.getAttribute("cartItemCount");
                }

                out.print("{\"success\":true,\"cartSubtotal\":\"" + cart.getSubtotal() + "\",\"cartItemCount\":" + totalItems + "}");
                out.flush();
                return;
            }

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, false, "Error removing item from cart");
            } else {
                throw new ServletException("Error while removing the item from cart.", e);
            }
            return;
        }

        // Only redirect if not an AJAX request
        if (!isAjaxRequest(request)) {
            response.sendRedirect("cart");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
