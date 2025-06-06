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
					<span class="bg-secondary pr-3"><fmt:message key="user.menu" /></span>
				</h5>
				<div class="list-group">
					<a href="profile"
						class="list-group-item list-group-item-action"> <fmt:message key="footer.profile" />
					</a> <a href="orderhistory"
						class="list-group-item list-group-item-action active"> <fmt:message key="footer.orderHistory" />
					</a>
					<c:if test="${user.hashedPassword != null}">
						<a href="changepassword" class="list-group-item list-group-item-action"><fmt:message key="footer.changePassword" /></a>
					</c:if>
				</div>
			</div>
		</div>

		<!-- Profile Section -->
		<div class="col-lg-9">
			<div class="bg-light p-30 mb-5">
				<h5 class="section-title position-relative text-uppercase mb-3">
					<span class="bg-secondary pr-3"><fmt:message key="footer.orderHistory" /></span>
				</h5>

				<c:if test="${not empty orders}">
					<div class="table-responsive">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th><fmt:message key="admin.id" /></th>
									<th><fmt:message key="orderhistory.orderDate" /></th>
									<th><fmt:message key="orderhistory.totalAmount" /></th>
									<th><fmt:message key="orderhistory.details" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="order" items="${orders}">
									<tr>
										<td>${order.id}</td>
										<td>${order.orderDate}</td>
										<td><fmt:formatNumber value="${order.total}" pattern="#,##0.## ₫"/></td>
										<td>
											<button type="button" class="btn btn-info"
												data-toggle="collapse" data-target="#details${order.id}">
												<fmt:message key="button.viewDetails" /></button>
										</td>
									</tr>
									<!-- Order Details -->
									<tr id="details${order.id}" class="collapse">
										<td colspan="4">
											<table class="table table-striped">
												<thead>
													<tr>
														<th><fmt:message key="orderhistory.product" /></th>
														<th><fmt:message key="orderhistory.variant" /></th>
														<th><fmt:message key="cart.quantity" /></th>
														<th><fmt:message key="cart.price" /></th>
														<th><fmt:message key="cart.total" /></th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="item" items="${order.orderDetails}">
														<tr>
															<td>${item.productName}</td>
															<td>${item.variantName}</td>
															<td>${item.quantity}</td>
															<td><fmt:formatNumber value="${item.price}" pattern="#,##0.## ₫"/></td>
															<td><fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.## ₫"/></td>
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
					<p class="text-center"><fmt:message key="orderhistory.noOrders" /></p>
				</c:if>
			</div>
		</div>
	</div>
</div>

<%@ include file="WEB-INF/footer.jsp"%>
