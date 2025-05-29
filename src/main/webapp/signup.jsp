<%@ include file="WEB-INF/header.jsp"%>
<body>
    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <h3 class="text-center"><fmt:message key="title.signup" /></h3>
                <form action="signup" method="post">
                    <div class="form-group">
                        <label for="username"><fmt:message key="label.username" /></label> 
                        <input type="text" name="username" id="username" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="password"><fmt:message key="label.password" /></label> 
                        <input type="password" name="password" id="password" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="repeatPassword"><fmt:message key="label.repeatPassword" /></label> 
                        <input type="password" name="repeatPassword" id="repeatPassword" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="firstName"><fmt:message key="label.firstName" /></label> 
                        <input type="text" name="firstName" id="firstName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="lastName"><fmt:message key="label.lastName" /></label> 
                        <input type="text" name="lastName" id="lastName" class="form-control" required>
                    </div>
                    <div class="form-group">
                        <label for="email"><fmt:message key="label.email" /></label> 
                        <input type="email" name="email" id="email" class="form-control" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-block">
                        <fmt:message key="button.signup" />
                    </button>
                </form>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger mt-3">${errorMessage}</div>
                </c:if>

            </div>
        </div>
    </div>
    <%@ include file="WEB-INF/footer.jsp"%>
</body>
