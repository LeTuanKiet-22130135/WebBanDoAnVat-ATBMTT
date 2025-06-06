<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="WEB-INF/header.jsp"%>
<!-- Include jQuery if not already included in header -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Include custom cart JavaScript -->
<script src="js/cart.js"></script>

<!-- Cart Start -->
<div class="container-fluid">
	<div class="row px-xl-5">
		<!-- Cart Items Table -->
		<div id="cart-items-container" class="col-lg-8 table-responsive mb-5">
			<%@ include file="WEB-INF/fragment/cart-items.jsp" %>
		</div>

		<!-- Cart Summary -->
		<div class="col-lg-4">
			<h5 class="section-title position-relative text-uppercase mb-3">
				<span class="bg-secondary pr-3"><fmt:message key="cart.summary" /></span>
			</h5>
			<div class="bg-light p-30 mb-5">
				<div class="border-bottom pb-2">
					<div class="d-flex justify-content-between mb-3">
						<h6><fmt:message key="cart.subtotal" /></h6>
						<h6><fmt:formatNumber value="${cartSubtotal}" pattern="#,##0.## ₫"/></h6>
					</div>
					<div class="d-flex justify-content-between">
						<h6 class="font-weight-medium"><fmt:message key="cart.shipping" /></h6>
						<h6 class="font-weight-medium"><fmt:formatNumber value="${shippingCost}" pattern="#,##0.## ₫"/></h6>
					</div>
				</div>
				<div class="pt-2">
					<div class="d-flex justify-content-between mt-2">
						<h5><fmt:message key="cart.total" /></h5>
						<h5><fmt:formatNumber value="${cartSubtotal.add(shippingCost)}" pattern="#,##0.## ₫"/></h5>
					</div>
					<a href="checkout.jsp"
						class="btn btn-block btn-primary font-weight-bold my-3 py-3">
						<fmt:message key="cart.proceedToCheckout" /> </a>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Cart End -->

<%@ include file="WEB-INF/footer.jsp"%>
