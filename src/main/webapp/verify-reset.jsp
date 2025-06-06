<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="WEB-INF/header.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h2 class="text-center">Verify Email</h2>
            <p class="text-center">A verification code has been sent to your email. Please enter it below.</p>
            
            <form action="verify-reset" method="post">
                <!-- Show error message if verification fails -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger">${errorMessage}</div>
                </c:if>

                <div class="form-group">
                    <label for="verificationCode">Verification Code</label> 
                    <input type="text" class="form-control" id="verificationCode" name="verificationCode" required>
                </div>

                <!-- Google reCAPTCHA -->
                <div class="form-group mt-3">
                    <div class="g-recaptcha" data-sitekey="6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI"
                        data-callback="enableVerifyButton"></div>
                </div>

                <div class="form-group mt-3">
                    <button type="submit" id="verifyButton" class="btn btn-primary btn-block" disabled>
                        Verify
                    </button>
                </div>
                
                <div class="mt-3 text-center">
                    <a href="login" class="text-primary">Back to Login</a>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Load reCAPTCHA script -->
<script src="https://www.google.com/recaptcha/api.js" async defer></script>

<script>
    // This function will be called by reCAPTCHA once the user completes it
    function enableVerifyButton() {
        document.getElementById('verifyButton').disabled = false;
    }
</script>

<%@ include file="WEB-INF/footer.jsp"%>