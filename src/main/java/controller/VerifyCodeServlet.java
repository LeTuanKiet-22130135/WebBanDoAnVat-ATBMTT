package controller;

import newdao.UserDAO;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/verify")
public class VerifyCodeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("verify.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String enteredCode = request.getParameter("verificationCode");
        HttpSession session = request.getSession();

        String actualCode = (String) session.getAttribute("verificationCode");

        if (enteredCode != null && enteredCode.equals(actualCode)) {
            // Verification successful: add user to database
            String username = (String) session.getAttribute("username");
            String hashedPassword = (String) session.getAttribute("hashedPassword");
            String firstName = (String) session.getAttribute("firstName");
            String lastName = (String) session.getAttribute("lastName");
            String email = (String) session.getAttribute("email");

            // Call UserDAO to insert the user
            UserDAO userDAO = new UserDAO();
            userDAO.addUserWithProfile(username, hashedPassword, firstName, lastName, email);

            // Clear session attributes
            session.invalidate();

            // Send JavaScript response to show a popup and redirect to login
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<script type='text/javascript'>");
            out.println("alert('Successfully created account!');");
            out.println("window.location.href = 'login';");
            out.println("</script>");
            out.println("</body></html>");
        } else {
            // Verification failed: show error message
            request.setAttribute("errorMessage", "Invalid verification code. Please try again.");
            request.getRequestDispatcher("verify.jsp").forward(request, response);
        }
    }
}
