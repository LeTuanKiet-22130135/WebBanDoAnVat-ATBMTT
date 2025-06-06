<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Success</title>
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
                <div class="card border-success">
                    <div class="card-header bg-success text-white">
                        <h4 class="mb-0">Payment Successful</h4>
                    </div>
                    <div class="card-body">
                        <div class="text-center mb-4">
                            <i class="fas fa-check-circle text-success" style="font-size: 5rem;"></i>
                        </div>
                        <h5 class="card-title text-center">Thank you for your payment!</h5>
                        <p class="card-text text-center">Your transaction has been completed successfully.</p>

                        <div class="mt-4">
                            <h6>Payment Details:</h6>
                            <table class="table table-bordered">
                                <tr>
                                    <th>Order Reference:</th>
                                    <td>${sessionScope.vnp_TxnRef}</td>
                                </tr>
                                <tr>
                                    <th>Amount:</th>
                                    <td>${Integer.parseInt(sessionScope.vnp_Amount)/100} VND</td>
                                </tr>
                                <tr>
                                    <th>Order Info:</th>
                                    <td>${sessionScope.vnp_OrderInfo}</td>
                                </tr>
                                <tr>
                                    <th>Bank:</th>
                                    <td>${sessionScope.vnp_BankCode}</td>
                                </tr>
                                <tr>
                                    <th>Payment Date:</th>
                                    <td>${sessionScope.vnp_PayDate}</td>
                                </tr>
                                <c:if test="${not empty sessionScope.ghnOrderCode}">
                                <tr>
                                    <th>Order Code:</th>
                                    <td>${sessionScope.ghnOrderCode}</td>
                                </tr>
                                </c:if>
                            </table>
                        </div>

                        <div class="text-center mt-4">
                            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Continue Shopping</a>
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
