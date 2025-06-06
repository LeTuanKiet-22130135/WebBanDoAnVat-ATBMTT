<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Failed</title>
    <!-- Include Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="css/style.css" rel="stylesheet">
</head>
<body>
    <!-- Include header -->
    <jsp:include page="/WEB-INF/header.jsp" />

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card border-danger">
                    <div class="card-header bg-danger text-white">
                        <h4 class="mb-0">Payment Failed</h4>
                    </div>
                    <div class="card-body">
                        <div class="text-center mb-4">
                            <i class="fas fa-times-circle text-danger" style="font-size: 5rem;"></i>
                        </div>
                        <h5 class="card-title text-center">Your payment was not successful</h5>
                        
                        <c:choose>
                            <c:when test="${sessionScope.paymentStatus eq 'failed'}">
                                <p class="card-text text-center">
                                    There was an issue processing your payment. 
                                    <c:if test="${not empty sessionScope.vnp_ResponseCode}">
                                        Error code: ${sessionScope.vnp_ResponseCode}
                                    </c:if>
                                </p>
                            </c:when>
                            <c:when test="${sessionScope.paymentStatus eq 'invalid'}">
                                <p class="card-text text-center">
                                    The payment verification failed. This could be due to data tampering or an invalid request.
                                </p>
                            </c:when>
                            <c:otherwise>
                                <p class="card-text text-center">
                                    An unknown error occurred during the payment process.
                                </p>
                            </c:otherwise>
                        </c:choose>
                        
                        <div class="alert alert-info mt-3">
                            <p class="mb-0">
                                <strong>What to do next?</strong><br>
                                - Check your payment details and try again<br>
                                - Try a different payment method<br>
                                - Contact our customer support if the problem persists
                            </p>
                        </div>
                        
                        <div class="text-center mt-4">
                            <a href="${pageContext.request.contextPath}/" class="btn btn-primary me-2">Return to Home</a>
                            <a href="${pageContext.request.contextPath}/checkout" class="btn btn-outline-primary">Try Again</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Include footer -->
    <jsp:include page="/WEB-INF/footer.jsp" />

    <!-- Bootstrap JS and Font Awesome -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
</body>
</html>