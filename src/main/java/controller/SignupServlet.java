package controller;

import util.PasswordUtil;
import util.CodeGenerator;
import util.EmailUtil;
import newdao.UserDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("signup.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String repeatPassword = request.getParameter("repeatPassword");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        try {
            // Check if passwords match
            if (!password.equals(repeatPassword)) {
                request.setAttribute("errorMessage", "Passwords do not match.");
                request.getRequestDispatcher("signup.jsp").forward(request, response);
                return;
            }

            // Check if username already exists
            if (userDAO.checkUser(username)) {
                request.setAttribute("errorMessage", "Username already exists. Please choose another one.");
                request.getRequestDispatcher("signup.jsp").forward(request, response);
                return;
            }

            // Hash the password using PasswordUtil
            String hashedPassword = PasswordUtil.hashPassword(password);

            // Generate verification code
            String verificationCode = CodeGenerator.generateVerificationCode();

            // Store user details and code in session (for verification step)
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("hashedPassword", hashedPassword); // Store hashed password
            session.setAttribute("firstName", firstName);
            session.setAttribute("lastName", lastName);
            session.setAttribute("email", email);
            session.setAttribute("verificationCode", verificationCode);

            // Send verification email
            EmailUtil.sendVerificationEmail(email, verificationCode);

            // Redirect to verify.jsp
            response.sendRedirect("verify");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error during signup. Please try again.");
            request.getRequestDispatcher("signup.jsp").forward(request, response);
        }
    }
}
