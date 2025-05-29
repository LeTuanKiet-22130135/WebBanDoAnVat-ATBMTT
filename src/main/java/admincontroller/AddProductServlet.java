package admincontroller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import model.Product;
import dao.ProductDAO;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;

@WebServlet("/admin/AddProductServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50   // 50MB
)
public class AddProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "C:\\Users\\asus\\git\\WebBanDoAnVat\\Web\\src\\main\\webapp\\img"; // Change this to your desired path
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward to the Add Product form
        request.getRequestDispatcher("AddProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Product product = new Product();
        product.setName(request.getParameter("name"));
        product.setDescription(request.getParameter("description"));
        product.setPrice(new BigDecimal(request.getParameter("price")));
        product.setQuantity(Integer.parseInt(request.getParameter("quantity")));

        // Handle file upload
        Part filePart = request.getPart("imageFile");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            // Ensure the upload directory exists
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Create directories if they do not exist
            }

            // Save the file to the upload directory
            String filePath = UPLOAD_DIR + File.separator + fileName;
            filePart.write(filePath);

            // Set the relative file path for the product image
            product.setImageUrl("img/" + fileName);
        }

        // Save the product to the database
        productDAO.addProduct(product);

        // Redirect back to ProductControl page
        response.sendRedirect("ProductControl");
    }
}
