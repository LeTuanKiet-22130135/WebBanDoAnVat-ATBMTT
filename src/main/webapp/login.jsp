<%@ include file="WEB-INF/header.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<body>
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h3 class="text-center"><fmt:message key="title.login" /></h3>
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    ${error}
                </div>
            </c:if>
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success" role="alert">
                    ${successMessage}
                </div>
            </c:if>
            <form action="login" method="post">
                <div class="form-group">
                    <label for="username"><fmt:message key="label.username" /></label>
                    <input type="text" name="username" id="username" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="password"><fmt:message key="label.password" /></label>
                    <input type="password" name="password" id="password" class="form-control" required>
                </div>
                <div class="d-flex justify-content-between">
                    <button type="submit" id="loginButton" class="btn btn-primary"><fmt:message key="button.login" /></button>
                    <a href="signup" class="btn btn-secondary"><fmt:message key="button.signup" /></a>
                </div>
                <div class="mt-2 text-center">
                    <a href="forgot-password" class="text-primary">Forgot Password?</a>
                </div>

                <div class="mt-3 text-center btn-group-vertical d-flex justify-content-center align-items-center">
                    <p>OR</p>
                    <a href="oauth/google" class="btn btn-danger mb-2">
                        <i class="fab fa-google mr-2"></i> Login with Google
                    </a>
                    <a href="oauth/facebook" class="btn" style="background-color: #4267B2; color: white;">
                        <i class="fab fa-facebook-f mr-2"></i> Login with Facebook
                    </a>
                </div>
                <c:if test="${loginLocked}">
                    <div class="mt-2 text-danger">
                        <span id="lockoutMessage">Login temporarily disabled. Please wait <span id="countdown"></span> seconds.</span>
                    </div>
                </c:if>
            </form>
        </div>
    </div>

<script>
    // Check if login is locked
    const isLocked = ${loginLocked != null ? loginLocked : false};
    const lockUntil = ${lockUntil != null ? lockUntil : 0};
    const loginButton = document.getElementById('loginButton');
    const countdownElement = document.getElementById('countdown');

    function updateLoginButton() {
        if (isLocked && lockUntil > Date.now()) {
            // Disable login button
            loginButton.disabled = true;

            // Calculate remaining time
            const remainingSeconds = Math.ceil((lockUntil - Date.now()) / 1000);

            // Update countdown
            if (countdownElement) {
                countdownElement.textContent = remainingSeconds;
            }

            // Check again in 1 second
            setTimeout(updateLoginButton, 1000);
        } else if (isLocked) {
            // Time's up, enable the button
            loginButton.disabled = false;

            // Hide the lockout message if it exists
            const lockoutMessage = document.getElementById('lockoutMessage');
            if (lockoutMessage) {
                lockoutMessage.style.display = 'none';
            }
        }
    }

    // Initialize the button state
    if (isLocked) {
        updateLoginButton();
    }
</script>

<%@ include file="WEB-INF/footer.jsp"%>
