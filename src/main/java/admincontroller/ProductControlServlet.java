package admincontroller;

import java.io.IOException;
import java.util.List;

import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;

@WebServlet("/admin/ProductControl")
public class ProductControlServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	String query = request.getParameter("query");

        List<Product> products;
        if (query != null && !query.trim().isEmpty()) {
            // Search products by name
            products = productDAO.searchProductsByName(query);
        } else {
            // Fetch all products if no query is provided
            products = productDAO.getAllProducts();
        }
    	
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            deleteProduct(request, response);
        } else {
        	request.setAttribute("products", products);
        	request.getRequestDispatcher("ProductControl.jsp").forward(request, response);
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            productDAO.deleteProduct(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Invalid product ID.");
        }
        // Reload the product list after deletion
        response.sendRedirect("ProductControl");
    }
}
