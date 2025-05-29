<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ include file="WEB-INF/header.jsp"%>

<div class="container-fluid mt-5">
	<div class="row px-xl-5">
		<!-- Side Navigation -->
		<div class="col-lg-3">
			<div class="bg-light p-30 mb-5">
				<h5 class="section-title position-relative text-uppercase mb-3">
					<span class="bg-secondary pr-3">User Menu</span>
				</h5>
				<div class="list-group">
					<a href="profile"
						class="list-group-item list-group-item-action"> Profile
					</a> <a href="orderhistory"
						class="list-group-item list-group-item-action active"> Order History
					</a>
					<a href="changepassword" class="list-group-item list-group-item-action">Change Password</a>
				</div>
			</div>
		</div>

		<!-- Profile Section -->
		<div class="col-lg-9">
			<div class="bg-light p-30 mb-5">
				<h5 class="section-title position-relative text-uppercase mb-3">
					<span class="bg-secondary pr-3">Order History</span>
				</h5>

				<c:if test="${not empty orders}">
					<div class="table-responsive">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th>Order ID</th>
									<th>Order Date</th>
									<th>Total Amount</th>
									<th>Details</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="order" items="${orders}">
									<tr>
										<td>${order.id}</td>
										<td>${order.orderDate}</td>
										<td>${order.total}đ</td>
										<td>
											<button type="button" class="btn btn-info"
												data-toggle="collapse" data-target="#details${order.id}">
												View Details</button>
										</td>
									</tr>
									<!-- Order Details -->
									<tr id="details${order.id}" class="collapse">
										<td colspan="4">
											<table class="table table-striped">
												<thead>
													<tr>
														<th>Product</th>
														<th>Variant</th>
														<th>Quantity</th>
														<th>Price</th>
														<th>Total</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="item" items="${order.orderDetails}">
														<tr>
															<td>${item.productName}</td>
															<td>${item.variantName}</td>
															<td>${item.quantity}</td>
															<td>${item.price}đ</td>
															<td>${item.totalPrice}đ</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:if>

				<c:if test="${empty orders}">
					<p class="text-center">You have no order history.</p>
				</c:if>
			</div>
		</div>
	</div>
</div>

<%@ include file="WEB-INF/footer.jsp"%>
