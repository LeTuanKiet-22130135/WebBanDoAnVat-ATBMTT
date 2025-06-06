package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import newdao.ReviewDAO;
import newdao.UserDAO;
import newmodel.User;

import java.io.IOException;

/**
 * Servlet for handling product review operations
 */
@WebServlet(urlPatterns = {"/review/add"})
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Handles POST requests for adding reviews
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        String username = (String) session.getAttribute("username");

        // Check if user is logged in
        if (username == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            // Get user ID from username
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                req.setAttribute("error", "User not found");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            // Get form parameters
            int productId = Integer.parseInt(req.getParameter("productId"));
            int rating = Integer.parseInt(req.getParameter("rating"));
            String comment = req.getParameter("comment");

            // Validate parameters
            if (productId <= 0 || rating < 1 || rating > 5 || comment == null || comment.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/detail?id=" + productId + "&error=invalid");
                return;
            }

            // Check if user has already reviewed this product
            if (reviewDAO.hasUserReviewedProduct(user.getId(), productId)) {
                resp.sendRedirect(req.getContextPath() + "/detail?id=" + productId + "&error=duplicate");
                return;
            }

            // Add the review
            int reviewId = reviewDAO.addReview(user.getId(), productId, rating, comment);

            if (reviewId > 0) {
                // Success - redirect back to product detail page
                resp.sendRedirect(req.getContextPath() + "/detail?id=" + productId + "&reviewSuccess=true");
            } else {
                // Error - redirect back with error message
                resp.sendRedirect(req.getContextPath() + "/detail?id=" + productId + "&error=failed");
            }

        } catch (NumberFormatException e) {
            // Invalid parameters - redirect to home
            resp.sendRedirect(req.getContextPath() + "/index");
        }
    }
}
