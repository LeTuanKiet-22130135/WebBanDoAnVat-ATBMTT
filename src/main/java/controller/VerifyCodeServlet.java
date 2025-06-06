package controller;

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
            // Check if this is a password change verification
            Boolean pendingPasswordChange = (Boolean) session.getAttribute("pendingPasswordChange");

            if (pendingPasswordChange != null && pendingPasswordChange) {
                // This is a password change verification
                String username = (String) session.getAttribute("username");
                String newHashedPassword = (String) session.getAttribute("newHashedPassword");

                if (username != null && newHashedPassword != null) {
                    // Update the password in the database
                    newdao.UserDAO userDAO = new newdao.UserDAO();
                    boolean updateSuccessful = userDAO.updateUserPassword(username, newHashedPassword);

                    // Remove verification attributes from session
                    session.removeAttribute("pendingPasswordChange");
                    session.removeAttribute("newHashedPassword");
                    session.removeAttribute("verificationCode");

                    // Send JavaScript response based on result
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("<html><body>");
                    out.println("<script type='text/javascript'>");

                    if (updateSuccessful) {
                        out.println("alert('Password changed successfully!');");
                    } else {
                        out.println("alert('Error updating password. Please try again.');");
                    }

                    out.println("window.location.href = 'changepassword';");
                    out.println("</script>");
                    out.println("</body></html>");
                } else {
                    // Missing required session attributes
                    request.setAttribute("errorMessage", "Session data missing. Please try changing your password again.");
                    request.getRequestDispatcher("changepassword.jsp").forward(request, response);
                }
            } else {
                // This is a new account verification
                String username = (String) session.getAttribute("username");
                String hashedPassword = (String) session.getAttribute("hashedPassword");
                String firstName = (String) session.getAttribute("firstName");
                String lastName = (String) session.getAttribute("lastName");
                String email = (String) session.getAttribute("email");

                // Call UserDAO to insert the user
                newdao.UserDAO userDAO = new newdao.UserDAO();
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
            }
        } else {
            // Verification failed: show error message
            request.setAttribute("errorMessage", "Invalid verification code. Please try again.");
            request.getRequestDispatcher("verify.jsp").forward(request, response);
        }
    }
}
