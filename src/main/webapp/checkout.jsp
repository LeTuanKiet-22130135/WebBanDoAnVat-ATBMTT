<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ include file="WEB-INF/header.jsp"%>

<!-- Checkout Start -->
<div class="container-fluid">
    <div class="row px-xl-5">
        <!-- Cart Items Section -->
        <div class="col-lg-8">
            <h5 class="section-title position-relative text-uppercase mb-3"><span class="bg-secondary pr-3">Your Cart</span></h5>
            <div class="bg-light p-30 mb-5">
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
                <c:if test="${empty cart.items}">
                    <p class="text-center">Your cart is empty.</p>
                </c:if>
            </div>
        </div>

        <!-- Order Summary & Payment Section -->
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
            </div>

            <!-- Payment Form -->
            <form action="checkout" method="post">
                <!-- Payment Section -->
                <div class="mb-5">
                    <h5 class="section-title position-relative text-uppercase mb-3"><span class="bg-secondary pr-3">Payment</span></h5>
                    <div class="bg-light p-30">
                        <div class="form-group">
                            <div class="custom-control custom-radio">
                                <input type="radio" class="custom-control-input" name="payment" id="cod" value="cod" required>
                                <label class="custom-control-label" for="cod">COD</label>
                            </div>
                            <div class="custom-control custom-radio mt-2">
                                <input type="radio" class="custom-control-input" name="payment" id="vnpay" value="vnpay">
                                <label class="custom-control-label" for="vnpay">
                                    VnPay
                                    <img src="https://sandbox.vnpayment.vn/paymentv2/images/icons/logo-en.svg" alt="VnPay" height="24" class="ml-2">
                                </label>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-block btn-primary font-weight-bold py-3">Place Order</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<!-- Checkout End -->

<%@ include file="WEB-INF/footer.jsp" %>
