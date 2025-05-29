<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="WEB-INF/header.jsp"%>

<!-- Cart Start -->
<div class="container-fluid">
	<div class="row px-xl-5">
		<!-- Cart Items Table -->
		<div class="col-lg-8 table-responsive mb-5">
			<table
				class="table table-light table-borderless table-hover text-center mb-0">
				<thead class="thead-dark">
					<tr>
						<th>Products</th>
						<th>Price</th>
						<th>Quantity</th>
						<th>Total</th>
						<th>Remove</th>
					</tr>
				</thead>
				<tbody class="align-middle">
					<!-- Loop through cart items -->
					<c:forEach var="item" items="${cart.items}">
						<tr>
							<td class="align-middle"><img src="${item.productImageUrl}"
								alt="${item.productName}" style="width: 50px;">
								${item.productName}</td>
							<td class="align-middle">$${item.price}</td>
							<td class="align-middle">
								<!-- Quantity update form -->
								<form method="post" action="UpdateCartServlet">
									<div class="input-group quantity mx-auto" style="width: 100px;">
										<div class="input-group-btn">
											<button type="submit" name="action" value="decrease"
												class="btn btn-sm btn-primary btn-minus"
												<c:if test="${item.quantity <= 1}">disabled</c:if>>
												<i class="fa fa-minus"></i>
											</button>
										</div>
										<input type="text"
											class="form-control form-control-sm bg-secondary border-0 text-center"
											name="quantity" value="${item.quantity}">
										<div class="input-group-btn">
											<button type="submit" name="action" value="increase"
												class="btn btn-sm btn-primary btn-plus">
												<i class="fa fa-plus"></i>
											</button>
										</div>
									</div>
									<input type="hidden" name="productId" value="${item.productId}">
									<input type="hidden" name="variantId" value="${item.variantId}">
								</form>
							</td>
							<td class="align-middle">$${item.totalPrice}</td>
							<td class="align-middle">
								<!-- Remove item button -->
								<form method="get" action="RemoveFromCartServlet">
									<input type="hidden" name="productId" value="${item.productId}">
									<input type="hidden" name="variantId" value="${item.variantId}">
									<button type="submit" class="btn btn-sm btn-danger">
										<i class="fa fa-times"></i>
									</button>
								</form>
							</td>
						</tr>
					</c:forEach>

					<!-- If the cart is empty -->
					<c:if test="${empty cart.items}">
						<tr>
							<td colspan="5" class="text-center">Your cart is empty!</td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</div>

		<!-- Cart Summary -->
		<div class="col-lg-4">
			<h5 class="section-title position-relative text-uppercase mb-3">
				<span class="bg-secondary pr-3">Cart Summary</span>
			</h5>
			<div class="bg-light p-30 mb-5">
				<div class="border-bottom pb-2">
					<div class="d-flex justify-content-between mb-3">
						<h6>Subtotal</h6>
						<h6>$${cartSubtotal}</h6>
					</div>
					<div class="d-flex justify-content-between">
						<h6 class="font-weight-medium">Shipping</h6>
						<h6 class="font-weight-medium">$${shippingCost}</h6>
					</div>
				</div>
				<div class="pt-2">
					<div class="d-flex justify-content-between mt-2">
						<h5>Total</h5>
						<h5>$${cartSubtotal.add(shippingCost)}</h5>
					</div>
					<a href="checkout.jsp"
						class="btn btn-block btn-primary font-weight-bold my-3 py-3">
						Proceed To Checkout </a>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Cart End -->

<%@ include file="WEB-INF/footer.jsp"%>
