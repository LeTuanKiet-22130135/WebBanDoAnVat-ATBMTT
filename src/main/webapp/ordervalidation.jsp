<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="WEB-INF/header.jsp"%>

<!-- Order Validation Start -->
<div class="container-fluid">
    <div class="row px-xl-5">
        <!-- Order Details Section -->
        <div class="col-lg-8">
            <h5 class="section-title position-relative text-uppercase mb-3"><span class="bg-secondary pr-3">Order Validation</span></h5>
            <div class="bg-light p-30 mb-5">
                <div class="alert alert-info">
                    <p>Please validate your order by following these steps:</p>
                    <ol>
                        <li>Click the "Generate Hash" button to download the order hash file</li>
                        <li>Sign the hash file using your private key to create a .sig file</li>
                        <li>Upload the signature file (.sig) using the form below</li>
                        <li>Click "Verify Signature" to proceed with your order</li>
                    </ol>
                </div>

                <h6 class="mb-3">Order Details</h6>
                <c:if test="${not empty cart.items}">
                    <table class="table table-bordered mb-5">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Price</th>
                                <th>Quantity</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${cart.items}">
                                <tr>
                                    <td>${item.productName}</td>
                                    <td><fmt:formatNumber value="${item.price}" pattern="#,##0.## ₫"/></td>
                                    <td>${item.quantity}</td>
                                    <td><fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.## ₫"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>

                <!-- Hash Generation Button -->
                <div class="mb-4">
                    <a href="invoicehash?paymentMethod=${paymentMethod}" class="btn btn-primary">
                        <i class="fa fa-download mr-1"></i> Generate Hash
                    </a>
                </div>

                <!-- Signature Upload Form -->
                <div>
                    <h6 class="mb-3">Upload Signature File</h6>
                    <form action="verifysignature" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="paymentMethod" value="${paymentMethod}">
                        <div class="form-group">
                            <label for="signatureFile">Select .sig file</label>
                            <input type="file" class="form-control-file" id="signatureFile" name="signatureFile" required>
                            <small class="form-text text-muted">
                                Upload the .sig file created by signing the hash with your private key.
                            </small>
                        </div>
                        <button type="submit" class="btn btn-success">Verify Signature</button>
                    </form>
                </div>

                <!-- Display Error Message if any -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger mt-3">
                        ${errorMessage}
                    </div>
                </c:if>
            </div>
        </div>

        <!-- Order Summary Section -->
        <div class="col-lg-4">
            <h5 class="section-title position-relative text-uppercase mb-3"><span class="bg-secondary pr-3">Order Summary</span></h5>
            <div class="bg-light p-30 mb-5">
                <div class="border-bottom">
                    <h6 class="mb-3">Products</h6>
                    <c:forEach var="item" items="${cart.items}">
                        <div class="d-flex justify-content-between">
                            <p>${item.productName}</p>
                            <p><fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.## ₫"/></p>
                        </div>
                    </c:forEach>
                </div>
                <div class="border-bottom pt-3 pb-2">
                    <div class="d-flex justify-content-between mb-3">
                        <h6>Subtotal</h6>
                        <h6><fmt:formatNumber value="${cart.getSubtotal()}" pattern="#,##0.## ₫"/></h6>
                    </div>
                    <div class="d-flex justify-content-between">
                        <h6 class="font-weight-medium">Shipping</h6>
                        <h6 class="font-weight-medium"><fmt:formatNumber value="${shippingCost}" pattern="#,##0.## ₫"/></h6>
                    </div>
                </div>
                <div class="pt-2">
                    <div class="d-flex justify-content-between mt-2">
                        <h5>Total</h5>
                        <h5><fmt:formatNumber value="${cart.getSubtotal().add(shippingCost)}" pattern="#,##0.## ₫"/></h5>
                    </div>
                </div>

                <!-- Payment Method -->
                <div class="border-bottom pt-3 pb-2">
                    <div class="d-flex justify-content-between">
                        <h6 class="font-weight-medium">Payment Method</h6>
                        <h6 class="font-weight-medium">
                            <c:choose>
                                <c:when test="${paymentMethod eq 'cod'}">Cash on Delivery</c:when>
                                <c:when test="${paymentMethod eq 'vnpay'}">VnPay</c:when>
                                <c:otherwise>Not selected</c:otherwise>
                            </c:choose>
                        </h6>
                    </div>
                </div>
            </div>

            <!-- Cancel Order Button -->
            <div class="mb-5">
                <a href="checkout" class="btn btn-block btn-secondary font-weight-bold py-3">Back to Checkout</a>
            </div>
        </div>
    </div>
</div>
<!-- Order Validation End -->

<%@ include file="WEB-INF/footer.jsp" %>
