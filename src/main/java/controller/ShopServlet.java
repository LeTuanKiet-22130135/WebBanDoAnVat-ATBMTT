package controller;

import newdao.ProductDAO;
import newdao.ReviewDAO;
import newdao.TypeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import newmodel.Product;
import newmodel.Type;

import java.io.IOException;
import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/shop")
public class ShopServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ProductDAO productDAO = new ProductDAO();
    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final TypeDAO typeDAO = new TypeDAO();
    private static final int PRODUCTS_PER_PAGE = 9;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryParam = req.getParameter("query");
        String[] priceRanges = req.getParameterValues("priceRange");
        String[] typeIds = req.getParameterValues("typeId");

        // Get pagination parameters
        int page = 0;
        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                page = Integer.parseInt(pageParam) - 1; // Convert from 1-based to 0-based
                if (page < 0) page = 0;
            }
        } catch (NumberFormatException e) {
            // If page parameter is invalid, default to first page
            page = 0;
        }

        // Get all product types for the filter
        List<Type> types = typeDAO.getAllTypes();

        // Get products for current page
        List<Product> products = productDAO.getProductsByCriteriaWithPagination(
            queryParam, priceRanges, typeIds, page, PRODUCTS_PER_PAGE);

        // Get total count for pagination
        int totalProducts = productDAO.getTotalProductCount(queryParam, priceRanges, typeIds);
        int totalPages = (int) Math.ceil((double) totalProducts / PRODUCTS_PER_PAGE);

        // Get ratings for each product
        Map<Integer, Double> productRatings = new HashMap<>();
        Map<Integer, Integer> productReviewCounts = new HashMap<>();

        for (Product product : products) {
            int productId = product.getId();
            double averageRating = reviewDAO.getAverageRatingByProductId(productId);
            int reviewCount = reviewDAO.getReviewCountByProductId(productId);

            productRatings.put(productId, averageRating);
            productReviewCounts.put(productId, reviewCount);
        }

        // Set attributes for the view
        req.setAttribute("products", products);
        req.setAttribute("currentPage", page + 1); // Convert back to 1-based for display
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("query", queryParam);
        req.setAttribute("priceRanges", priceRanges);
        req.setAttribute("typeIds", typeIds);
        req.setAttribute("types", types);
        req.setAttribute("productRatings", productRatings);
        req.setAttribute("productReviewCounts", productReviewCounts);

        String ajax = req.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(ajax)) {
            req.getRequestDispatcher("WEB-INF/fragment/product-list.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("shop.jsp").forward(req, resp);
        }
    }
}
