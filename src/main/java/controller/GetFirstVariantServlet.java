package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import newdao.VariantDAO;
import newmodel.Variant;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to get the first variant ID for a product
 * Used by the cart.js to get a valid variant ID when adding a product to cart from listing pages
 */
@WebServlet("/getFirstVariant")
public class GetFirstVariantServlet extends HttpServlet {

    private final VariantDAO variantDAO = new VariantDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response content type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Get product ID from request
        String productIdParam = request.getParameter("productId");
        PrintWriter out = response.getWriter();
        
        // Validate product ID
        if (productIdParam == null || productIdParam.isEmpty()) {
            out.print("{\"success\":false,\"message\":\"Product ID is required\"}");
            return;
        }
        
        try {
            int productId = Integer.parseInt(productIdParam);
            
            // Get first variant for the product
            Variant variant = variantDAO.getFirstVariantByProductId(productId);
            
            if (variant != null) {
                // Return variant ID as JSON
                out.print("{\"success\":true,\"variantId\":" + variant.getId() + "}");
            } else {
                // No variant found
                out.print("{\"success\":false,\"message\":\"No variant found for product\"}");
            }
        } catch (NumberFormatException e) {
            out.print("{\"success\":false,\"message\":\"Invalid product ID\"}");
        }
    }
}