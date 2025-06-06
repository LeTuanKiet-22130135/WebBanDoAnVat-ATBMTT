<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="WEB-INF/header.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center">Reset Password</h2>
            <p class="text-center">Please enter and confirm your new password</p>
            
            <form action="reset-password" method="post">
                <!-- Show error message if there's an error -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">${errorMessage}</div>
                </c:if>

                <div class="form-group">
                    <label for="password">New Password</label> 
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>

                <div class="form-group mt-3">
                    <label for="confirmPassword">Confirm New Password</label> 
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                </div>

                <div class="form-group mt-3">
                    <button type="submit" class="btn btn-primary btn-block">
                        Reset Password
                    </button>
                </div>
                
                <div class="mt-3 text-center">
                    <a href="login" class="text-primary">Back to Login</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    // Simple client-side validation to check if passwords match
    document.querySelector('form').addEventListener('submit', function(e) {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        
        if (password !== confirmPassword) {
            e.preventDefault();
            alert('Passwords do not match!');
        }
    });
</script>

<%@ include file="WEB-INF/footer.jsp"%>