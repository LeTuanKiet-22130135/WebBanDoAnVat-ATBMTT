package controller;
import java.io.IOException;
import java.io.Serial;
import java.util.List;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpSession;
import newdao.ProductDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import newmodel.Product;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;
	private final ProductDAO dao = new ProductDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		List<Product> allProducts = dao.getAllProducts();
		// Limit to 8 products for display on index page
		List<Product> limitedProducts = allProducts.size() > 8 ? allProducts.subList(0, 8) : allProducts;
		HttpSession s = req.getSession(true);
		s.setAttribute("products", limitedProducts);
		RequestDispatcher dispatcher = req.getRequestDispatcher("index.jsp");
		dispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}
}
