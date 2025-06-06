package controller;

import newdao.UserDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import newmodel.User;
import newmodel.UserProfile;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get user ID from session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        int userId = userDAO.getUserIdByUsername(username);

        // Fetch profile from the database
        UserProfile profile = userDAO.getUserProfileByUserId(userId);
        System.out.println(profile.toString());

        // Fetch user object to check if user is logged in through Google
        User user = userDAO.getUserByUsername(username);

        // Set profile and user attributes and forward to JSP
        request.setAttribute("profile", profile);
        request.setAttribute("user", user);
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get user ID from the session
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        int userId = userDAO.getUserIdByUsername(username);

        // Create profile object and populate fields
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        profile.setFirstName(request.getParameter("firstName"));
        profile.setLastName(request.getParameter("lastName"));
        profile.setEmail(request.getParameter("email"));
        profile.setMobileNo(request.getParameter("mobileNo"));
        profile.setAddressLine1(request.getParameter("addressLine1"));
        profile.setAddressLine2(request.getParameter("addressLine2"));
        profile.setCountry(request.getParameter("country"));
        profile.setCity(request.getParameter("city"));
        profile.setState(request.getParameter("state"));
        profile.setZipCode(request.getParameter("zipCode"));

        // Update the profile in the database
        userDAO.updateUserProfile(profile);

        // Redirect back to profile page
        response.sendRedirect("profile");
    }
}
