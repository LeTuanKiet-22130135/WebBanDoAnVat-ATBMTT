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
					</a>
					<a href="pubkey" class="list-group-item list-group-item-action">
						Public Key Management
					</a>
					<a href="orderhistory"
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

				<!-- Display success or error messages -->
				<c:if test="${not empty successMessage}">
					<div class="alert alert-success" role="alert">
						${successMessage}
					</div>
				</c:if>
				<c:if test="${not empty errorMessage}">
					<div class="alert alert-danger" role="alert">
						${errorMessage}
					</div>
				</c:if>

				<c:if test="${not empty orders}">
					<div class="table-responsive">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th><fmt:message key="orderhistory.orderDate" /></th>
									<th><fmt:message key="orderhistory.totalAmount" /></th>
									<th>Verified</th>
									<th><fmt:message key="orderhistory.details" /></th>
									<th>Integrity Check</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="order" items="${orders}">
									<tr>
										<td>${order.orderDate}</td>
										<td><fmt:formatNumber value="${order.total}" pattern="#,##0.## ₫"/></td>
										<td>
											<c:choose>
												<c:when test="${order.verify}">
													<span class="badge badge-success">Yes</span>
												</c:when>
												<c:otherwise>
													<span class="badge badge-secondary">No</span>
												</c:otherwise>
											</c:choose>
										</td>
										<td>
											<button type="button" class="btn btn-info"
												data-toggle="collapse" data-target="#details${order.id}">
												<fmt:message key="button.viewDetails" /></button>
										</td>
										<td>
											<c:if test="${order.verify && order.signature != null}">
												<a href="checkintegrity?orderId=${order.id}" class="btn btn-warning">
													Check Integrity
												</a>
											</c:if>
										</td>
									</tr>
									<!-- Order Details -->
									<tr id="details${order.id}" class="collapse">
										<td colspan="5">
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
