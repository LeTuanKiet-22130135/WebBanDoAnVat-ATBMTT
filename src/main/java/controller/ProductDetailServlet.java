package controller;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

import newdao.ProductDAO;
import newdao.ReviewDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import newmodel.Product;
import newmodel.Review;

@WebServlet("/detail")
public class ProductDetailServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;
	private final ProductDAO productDAO = new ProductDAO();
	private final ReviewDAO reviewDAO = new ReviewDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String productId = req.getParameter("id");
		if (productId == null || productId.isEmpty()) {
			resp.sendRedirect("index.jsp");
			return;
		}

		int pId = Integer.parseInt(productId);
		Product product = productDAO.getProductById(pId);

		// Load reviews for the product
		List<Review> reviews = reviewDAO.getReviewsByProductId(pId);
		int reviewCount = reviewDAO.getReviewCountByProductId(pId);
		double averageRating = reviewDAO.getAverageRatingByProductId(pId);

		// Set attributes for the JSP
		req.setAttribute("product", product);
		req.setAttribute("reviews", reviews);
		req.setAttribute("reviewCount", reviewCount);
		req.setAttribute("averageRating", averageRating);

		// Handle error parameters
		String error = req.getParameter("error");
		if (error != null) {
			switch (error) {
				case "invalid":
					req.setAttribute("reviewError", "Please provide a valid rating and comment.");
					break;
				case "duplicate":
					req.setAttribute("reviewError", "You have already reviewed this product.");
					break;
				case "failed":
					req.setAttribute("reviewError", "Failed to add your review. Please try again later.");
					break;
				default:
					req.setAttribute("reviewError", "An error occurred while submitting your review.");
					break;
			}
		}

		req.getRequestDispatcher("detail.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

}
