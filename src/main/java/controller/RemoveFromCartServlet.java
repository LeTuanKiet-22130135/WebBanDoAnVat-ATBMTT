package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

import newdao.CartDAO;
import newdao.UserDAO;
import newmodel.Cart;
import newmodel.User;

@WebServlet("/RemoveFromCartServlet")
public class RemoveFromCartServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CartDAO cartDAO = new CartDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {   

        // Get session and check if user is logged in
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");
        if (username == null) {
            response.sendRedirect("login");
            return;
        }

        String productIdParam = request.getParameter("productId");
        String variantIdParam = request.getParameter("variantId");

        if (productIdParam == null || productIdParam.isEmpty()) {
            response.sendRedirect("cart");
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
                response.sendRedirect("cart");
                return;
            }

            int cartId = cartDAO.getCartIdByUsername(username);
            if (cartId == -1) {
                response.sendRedirect("cart");
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
            }

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error while removing the item from cart.", e);
        }

        // Redirect back to the cart page
        response.sendRedirect("cart");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
