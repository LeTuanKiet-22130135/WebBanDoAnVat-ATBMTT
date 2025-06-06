<%@ include file="WEB-INF/header.jsp"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h3 class="text-center">Forgot Password</h3>
                <p class="text-center">Enter your username to reset your password</p>
                
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" role="alert">
                        ${errorMessage}
                    </div>
                </c:if>
                
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success" role="alert">
                        ${successMessage}
                    </div>
                </c:if>
                
                <form action="forgot-password" method="post">
                    <div class="form-group">
                        <label for="username">Username</label>
                        <input type="text" name="username" id="username" class="form-control" required>
                    </div>
                    
                    <div class="form-group mt-3">
                        <button type="submit" class="btn btn-primary btn-block">Continue</button>
                    </div>
                    
                    <div class="mt-3 text-center">
                        <a href="login" class="text-primary">Back to Login</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
<%@ include file="WEB-INF/footer.jsp"%>