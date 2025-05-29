package controller;

import java.io.IOException;
import java.io.Serial;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import newdao.UserDAO;

@WebServlet(urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;

	private final UserDAO userDAO = new UserDAO();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.getRequestDispatcher("login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");

		// Validate input
		if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
			req.setAttribute("error", "Username and password are required.");
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}

		// Check if user exists
		if (!userDAO.checkUser(username)) {
			req.setAttribute("error", "Invalid username or password.");
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}

		// Check if password is correct
		if (!userDAO.checkPassword(username, password)) {
			req.setAttribute("error", "Invalid username or password.");
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}

		// Authentication successful, create session
		HttpSession session = req.getSession();
		session.setAttribute("username", username);

		// Redirect to index page
		resp.sendRedirect("index");
	}
}
