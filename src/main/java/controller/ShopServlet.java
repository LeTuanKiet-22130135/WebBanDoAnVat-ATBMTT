package controller;

import newdao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import newmodel.Product;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

@WebServlet("/shop")
public class ShopServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ProductDAO productDAO = new ProductDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String queryParam = req.getParameter("query");
        String[] priceRanges = req.getParameterValues("priceRange");

        List<Product> products = productDAO.getProductsByCriteria(queryParam, priceRanges);

        req.setAttribute("products", products);

        String ajax = req.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(ajax)) {
            req.getRequestDispatcher("WEB-INF/fragment/product-list.jsp").forward(req, resp);
        } else {
            req.getRequestDispatcher("shop.jsp").forward(req, resp);
        }
    }
}
