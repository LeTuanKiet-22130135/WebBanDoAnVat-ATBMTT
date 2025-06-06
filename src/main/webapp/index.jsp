<%@ page contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="WEB-INF/header.jsp"%>
<!-- Include jQuery if not already included in header -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Include custom cart JavaScript -->
<script src="${pageContext.request.contextPath}/js/cart.js"></script>

<!-- Carousel Start -->
<div class="container-fluid mb-3">
	<div class="row px-xl-5">
		<div class="col-lg-8">
			<div id="header-carousel"
				class="carousel slide carousel-fade mb-30 mb-lg-0"
				data-ride="carousel">
				<ol class="carousel-indicators">
					<li data-target="#header-carousel" data-slide-to="0" class="active"></li>
					<li data-target="#header-carousel" data-slide-to="1"></li>
					<li data-target="#header-carousel" data-slide-to="2"></li>
				</ol>
				<div class="carousel-inner">
					<div class="carousel-item position-relative active"
						style="height: 430px;">
						<img class="position-absolute w-100 h-100"
							src="img/carousel-1.jpg" style="object-fit: cover;" alt="">
						<div
							class="carousel-caption d-flex flex-column align-items-center justify-content-center">
							<div class="p-3" style="max-width: 700px;">
								<h1 class="display-4 text-white mb-3 animate__animated animate__fadeInDown">
									<fmt:message key="carousel.dryfood" />
								</h1>
								<p class="mx-md-5 px-5 animate__animated animate__bounceIn">
									<fmt:message key="carousel.dryfoodDest" />
								</p>
								<a
									class="btn btn-outline-light py-2 px-4 mt-3 animate__animated animate__fadeInUp"
									href="detail?id=10"><fmt:message key="button.shopNow" /></a>
							</div>
						</div>
					</div>
					<div class="carousel-item position-relative" style="height: 430px;">
						<img class="position-absolute w-100 h-100"
							src="img/carousel-2.jpg" style="object-fit: cover;">
						<div
							class="carousel-caption d-flex flex-column align-items-center justify-content-center">
							<div class="p-3" style="max-width: 700px;">
								<h1
									class="display-4 text-white mb-3 animate__animated animate__fadeInDown">
									<fmt:message key="carousel.foodbanner"/></h1>
								<p class="mx-md-5 px-5 animate__animated animate__bounceIn"><fmt:message key="carousel.foodbannerDest" /></p>
								<a
									class="btn btn-outline-light py-2 px-4 mt-3 animate__animated animate__fadeInUp"
									href="detail?id=9"><fmt:message key="button.shopNow" /></a>
							</div>
						</div>
					</div>
					<div class="carousel-item position-relative" style="height: 430px;">
						<img class="position-absolute w-100 h-100"
							src="img/carousel-3.jpg" style="object-fit: cover;">
						<div
							class="carousel-caption d-flex flex-column align-items-center justify-content-center">
							<div class="p-3" style="max-width: 700px;">
								<h1
									class="display-4 text-white mb-3 animate__animated animate__fadeInDown"><fmt:message key="carousel.tea" /></h1>
								<p class="mx-md-5 px-5 animate__animated animate__bounceIn">
									<fmt:message key="carousel.teaDest" />
								</p>
								<a
									class="btn btn-outline-light py-2 px-4 mt-3 animate__animated animate__fadeInUp"
									href="detail?id=8"><fmt:message key="button.shopNow" /></a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="col-lg-4">
			<div class="product-offer mb-30" style="height: 200px;">
				<img class="img-fluid" src="img/offer-1.jpg" alt="">
				<div class="offer-text">
					<h3 class="text-white mb-3"><fmt:message key="carousel.specialOffer" /></h3>
					<a href="shop.jsp" class="btn btn-primary"><fmt:message key="button.shopNow" /></a>
				</div>
			</div>
			<div class="product-offer mb-30" style="height: 200px;">
				<img class="img-fluid" src="img/offer-2.jpg" alt="">
				<div class="offer-text">
					<h3 class="text-white mb-3"><fmt:message key="carousel.specialOffer" /></h3>
					<a href="shop.jsp" class="btn btn-primary"><fmt:message key="button.shopNow" /></a>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Carousel End -->

<!-- Featured Start -->
<div class="container-fluid pt-5">
	<div class="row px-xl-5 pb-3">
		<div class="col-lg-3 col-md-6 col-sm-12 pb-1">
			<div class="d-flex align-items-center bg-light mb-4"
				style="padding: 30px;">
				<h1 class="fa fa-check text-primary m-0 mr-3"></h1>
				<h5 class="font-weight-semi-bold m-0"><fmt:message key="featured.quality"/></h5>
			</div>
		</div>
		<div class="col-lg-3 col-md-6 col-sm-12 pb-1">
			<div class="d-flex align-items-center bg-light mb-4"
				style="padding: 30px;">
				<h1 class="fa fa-shipping-fast text-primary m-0 mr-2"></h1>
				<h5 class="font-weight-semi-bold m-0"><fmt:message key="featured.shipping"/></h5>
			</div>
		</div>
		<div class="col-lg-3 col-md-6 col-sm-12 pb-1">
			<div class="d-flex align-items-center bg-light mb-4"
				style="padding: 30px;">
				<h1 class="fas fa-exchange-alt text-primary m-0 mr-3"></h1>
				<h5 class="font-weight-semi-bold m-0"><fmt:message key="featured.return"/></h5>
			</div>
		</div>
		<div class="col-lg-3 col-md-6 col-sm-12 pb-1">
			<div class="d-flex align-items-center bg-light mb-4"
				style="padding: 30px;">
				<h1 class="fa fa-phone-volume text-primary m-0 mr-3"></h1>
				<h5 class="font-weight-semi-bold m-0"><fmt:message key="featured.support"/></h5>
			</div>
		</div>
	</div>
</div>
<!-- Featured End -->

<!-- Products Start -->
<div class="container-fluid pt-5 pb-3">
    <h2 class="section-title position-relative text-uppercase mx-xl-5 mb-4">
        <span class="bg-secondary pr-3"><fmt:message key="products.recent"/></span>
    </h2>
    <div class="row px-xl-5">
        <c:forEach var="product" items="${sessionScope.products}">
            <div class="col-lg-3 col-md-4 col-sm-6 pb-1">
                <div class="product-item bg-light mb-4">
                    <div class="product-img position-relative overflow-hidden">
                        <img class="img-fluid w-100" src="${product.img}" alt="${product.name}" style="width: 433px; height: 433px; object-fit: contain;">
                        <div class="product-action">
                            <a class="btn btn-outline-dark btn-square add-to-cart-btn" href="javascript:void(0)" data-product-id="${product.id}"><i class="fa fa-shopping-cart"></i></a>
                            <a class="btn btn-outline-dark btn-square" href="detail?id=${product.id}"><i class="fa fa-search"></i></a>
                        </div>
                    </div>
                    <div class="text-center py-4">
                        <a class="h6 text-decoration-none text-truncate" href="detail?id=${product.id}">${product.name}</a>
                        <div class="d-flex align-items-center justify-content-center mt-2">
                            <h5><fmt:formatNumber value="${product.price}" pattern="#,##0.## ₫"/></h5>
                        </div>
                        <div class="d-flex align-items-center justify-content-center mb-1">
                            <c:set var="rating" value="${sessionScope.productRatings[product.id] != null ? sessionScope.productRatings[product.id] : 0}" />
                            <c:set var="reviewCount" value="${sessionScope.productReviewCounts[product.id] != null ? sessionScope.productReviewCounts[product.id] : 0}" />

                            <c:set var="integerPart" value="${rating - (rating % 1)}" />
                            <c:set var="decimalPart" value="${rating % 1}" />

                            <c:forEach begin="1" end="5" var="i">
                                <c:choose>
                                    <c:when test="${i <= integerPart}">
                                        <small class="fas fa-star text-primary mr-1"></small>
                                    </c:when>
                                    <c:when test="${i == integerPart + 1 && decimalPart >= 0.5}">
                                        <small class="fas fa-star-half-alt text-primary mr-1"></small>
                                    </c:when>
                                    <c:otherwise>
                                        <small class="far fa-star text-primary mr-1"></small>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <small>(${reviewCount})</small>
                        </div>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>
<!-- Products End -->

<script>
    $(document).ready(function() {
        // Initialize product listing page functionality
        initProductListingPage();
    });
</script>

<%@ include file="WEB-INF/footer.jsp"%>
