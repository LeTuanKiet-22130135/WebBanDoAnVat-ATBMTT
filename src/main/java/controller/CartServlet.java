package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import newmodel.*;

import java.io.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import newdao.*;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        String username = (String) session.getAttribute("username");
        if (username == null) { 
            response.sendRedirect("login");
            return;
        }
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            throw new ServletException("User not found for username: " + username);
        }
        int userId = user.getId();

        try {
            int cartId = cartDAO.getOrCreateCartId(userId);
            List<CartItem> items = cartDAO.getCartItems(cartId);

            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setId(cartId);
            cart.setItems(items);

            session.setAttribute("cart", cart);
            session.setAttribute("cartSubtotal", cart.getSubtotal());
            session.setAttribute("shippingCost", new BigDecimal("5.00"));

            // Calculate total items count and store in session
            int totalItems = 0;
            if (cart.getItems() != null) {
                for (CartItem item : cart.getItems()) {
                    totalItems += item.getQuantity();
                }
            }
            session.setAttribute("cartItemCount", totalItems);
            request.getRequestDispatcher("cart.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error while retrieving cart");
        }
    }

    // Helper method to check if request is an AJAX request
    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    // Helper method to send JSON response
    private void sendJsonResponse(HttpServletResponse response, boolean success, String message, int cartItemCount, BigDecimal cartSubtotal) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"success\":" + success + ",\"message\":\"" + message + "\",\"cartItemCount\":" + cartItemCount + ",\"cartSubtotal\":\"" + cartSubtotal + "\"}");
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            System.out.println("No username in session");
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, false, "User not logged in", 0, BigDecimal.ZERO);
            } else {
                response.sendRedirect("login");
            }
            return;
        }
        User user = userDAO.getUserByUsername(username);
        if (user == null) {
            System.out.println("User not found");
            if (isAjaxRequest(request)) {
                sendJsonResponse(response, false, "User not found", 0, BigDecimal.ZERO);
            } else {
                response.sendRedirect("login");
            }
            return;
        }
        int userId = user.getId();

        String action = request.getParameter("action");
        int productId = Integer.parseInt(request.getParameter("productId"));
        // Get variant ID from request, default to 0 if not provided
        int variantId = 0;
        String variantIdParam = request.getParameter("variantId");
        System.out.println("variantIdParam = " + variantIdParam);
        if (variantIdParam != null && !variantIdParam.isEmpty()) {
            variantId = Integer.parseInt(variantIdParam);
            System.out.println("variantId = " + variantId);
        }

        // Get quantity from request, default to 1 if not provided
        int quantity = 1;
        String quantityParam = request.getParameter("quantity");
        if (quantityParam != null && !quantityParam.isEmpty()) {
            try {
                quantity = Integer.parseInt(quantityParam);
                if (quantity < 1) {
                    quantity = 1; // Ensure quantity is at least 1
                }
            } catch (NumberFormatException e) {
                // If quantity is not a valid number, default to 1
                quantity = 1;
            }
        }

        if ("add".equals(action)) {
            try {
                int cartId = cartDAO.getOrCreateCartId(userId);
                System.out.println( "addOrUpdateCartItem: cartId = " + cartId + ", productId = " + productId + ", variantId = " + variantId);

                // First add the item to the cart (this will add 1 quantity)
                cartDAO.addOrUpdateCartItem(cartId, productId, variantId);

                // If quantity is more than 1, update the quantity
                if (quantity > 1) {
                    // Update quantity to be (quantity - 1) more than current quantity
                    // Since addOrUpdateCartItem already added 1
                    cartDAO.updateCartItemQuantity(cartId, productId, variantId, quantity - 1);
                }

                // Get updated cart and set in session
                Cart cart = cartDAO.getCartByUserId(userId);
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

            } catch (SQLException e) {
                e.printStackTrace();
                if (isAjaxRequest(request)) {
                    sendJsonResponse(response, false, "Database error while adding product to cart", 0, BigDecimal.ZERO);
                } else {
                    throw new ServletException("Database error while adding product to cart");
                }
                return;
            }

            // If it's an AJAX request, send a JSON response
            if (isAjaxRequest(request)) {
                int totalItems = 0;
                BigDecimal cartSubtotal = BigDecimal.ZERO;

                // Get values from session
                if (session.getAttribute("cartItemCount") != null) {
                    totalItems = (int) session.getAttribute("cartItemCount");
                }

                if (session.getAttribute("cartSubtotal") != null) {
                    cartSubtotal = (BigDecimal) session.getAttribute("cartSubtotal");
                }

                sendJsonResponse(response, true, "Product added to cart successfully", totalItems, cartSubtotal);
                return;
            }
        }

        // Only redirect if not an AJAX request
        response.sendRedirect("cart");
    }
}
