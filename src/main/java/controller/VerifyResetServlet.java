package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/verify-reset")
public class VerifyResetServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Check if there's a pending password reset
        HttpSession session = request.getSession();
        Boolean resetPasswordPending = (Boolean) session.getAttribute("resetPasswordPending");
        
        if (resetPasswordPending == null || !resetPasswordPending) {
            // No pending password reset, redirect to forgot password page
            response.sendRedirect("forgot-password");
            return;
        }
        
        // Forward to the verification page
        RequestDispatcher dispatcher = request.getRequestDispatcher("verify-reset.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Get the verification code from the form
        String enteredCode = request.getParameter("verificationCode");
        
        // Get the session
        HttpSession session = request.getSession();
        
        // Get the actual verification code from the session
        String actualCode = (String) session.getAttribute("verificationCode");
        Boolean resetPasswordPending = (Boolean) session.getAttribute("resetPasswordPending");
        
        // Check if there's a pending password reset and the code matches
        if (resetPasswordPending != null && resetPasswordPending && 
            enteredCode != null && enteredCode.equals(actualCode)) {
            
            // Code is valid, redirect to reset password page
            response.sendRedirect("reset-password");
        } else {
            // Code is invalid, show error message
            request.setAttribute("errorMessage", "Invalid verification code. Please try again.");
            request.getRequestDispatcher("verify-reset.jsp").forward(request, response);
        }
    }
}