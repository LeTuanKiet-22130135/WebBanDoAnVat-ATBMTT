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
            response.sendRedirect("login");
            return;
        }

        int productId = Integer.parseInt(request.getParameter("productId"));
        String action = request.getParameter("action");

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
                response.sendRedirect("cart");
                return;
            }

            // Fetch or validate the cart ID using CartDAO
            int cartId = cartDAO.getCartIdByUsername(username);
            if (cartId == -1) {
                response.sendRedirect("cart");
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error updating cart item quantity.");
        }

        // Redirect back to cart page
        response.sendRedirect("cart");
    }
}
