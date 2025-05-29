package controller;

import java.io.IOException;
import java.io.Serial;

import newdao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import newmodel.Product;

@WebServlet("/detail")
public class ProductDetailServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;
	private final ProductDAO dao = new ProductDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String productId = req.getParameter("id");
		if (productId == null || productId.isEmpty()) {
			resp.sendRedirect("index.jsp");
			return;
		}
		Product product = dao.getProductById(Integer.parseInt(productId));

		req.setAttribute("product", product);
		req.getRequestDispatcher("detail.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

}
