package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/setLanguage")
public class SetLanguageServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lang = request.getParameter("lang");
        if (lang != null) {
            // Set language in a cookie
            Cookie langCookie = new Cookie("lang", lang);
            langCookie.setMaxAge(60 * 60 * 24 * 30); // Expires in 30 days
            response.addCookie(langCookie);
        }
        // Redirect back to the previous page
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : "index.jsp");
    }
}
