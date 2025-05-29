package admincontroller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import model.Product;
import dao.ProductDAO;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;

@WebServlet("/admin/UpdateProductServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50   // 50MB
)
public class UpdateProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "img/";
    private ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        // Fetch product details by ID
        Product product = productDAO.getProductById(id);

        if (product == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
            return;
        }

        // Set product as a request attribute and forward to the JSP
        request.setAttribute("product", product);
        request.getRequestDispatcher("UpdateProduct.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Product product = new Product();
        product.setId(Integer.parseInt(request.getParameter("id")));
        product.setName(request.getParameter("name"));
        product.setDescription(request.getParameter("description"));
        product.setPrice(new BigDecimal(request.getParameter("price")));
        product.setQuantity(Integer.parseInt(request.getParameter("quantity")));

        // Define the absolute path to your workspace's `img` folder
        String workspaceImgPath = "C:\\Users\\asus\\git\\WebBanDoAnVat\\Web\\src\\main\\webapp\\img";

        // Handle file upload
        Part filePart = request.getPart("imageFile");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

            // Save the file to the workspace's `img` folder
            File uploadDir = new File(workspaceImgPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String filePath = workspaceImgPath + File.separator + fileName;
            System.out.println("Saving file to: " + filePath);
            filePart.write(filePath);

            // Set the relative file path for the product image
            product.setImageUrl(UPLOAD_DIR + fileName);
        } else {
            // If no file is uploaded, retain the current image URL
            product.setImageUrl(request.getParameter("currentImageUrl"));
        }

        // Update the product in the database
        productDAO.updateProduct(product);

        // Redirect back to ProductControl page
        response.sendRedirect("ProductControl");
    }

}
