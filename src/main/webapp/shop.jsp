<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="WEB-INF/header.jsp"%>
<!-- Include jQuery if not already included in header -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Include custom cart JavaScript -->
<script src="${pageContext.request.contextPath}/js/cart.js"></script>

<div class="container-fluid">
    <div class="row">
        <!-- Filter Sidebar Start -->
        <div class="col-lg-3 col-md-4">
            <h5 class="section-title position-relative text-uppercase mb-3">
                <span class="bg-secondary pr-3"><fmt:message key="shop.fliterByPrice"/></span>
            </h5>
            <div class="bg-light p-4 mb-30">
                <!-- Price Filter Form -->
                <form id="price-filter-form">
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="all" id="price-all">
                        <label class="custom-control-label" for="price-all"><fmt:message key="shop.allPrices"/></label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="0-10000" id="price-1" >
                        <label class="custom-control-label" for="price-1">0 - 10,000 ₫</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="10001-20000" id="price-2">
                        <label class="custom-control-label" for="price-2">10,000 - 20,000 ₫</label>

                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="20001-30000" id="price-3">
                        <label class="custom-control-label" for="price-3">20,000 - 30,000 ₫</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="30001-40000" id="price-4">
                        <label class="custom-control-label" for="price-4">30,000 - 40,000 ₫</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="40001-50000" id="price-5">
                        <label class="custom-control-label" for="price-5">40,000 - 50,000 ₫</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="over50000" id="price-morethan">
                        <label class="custom-control-label" for="price-morethan"><fmt:message key="shop.morethan"/> 50,000 ₫</label>
                    </div>
                </form>
            </div>

            <!-- Product Type Filter -->
            <h5 class="section-title position-relative text-uppercase mb-3">
                <span class="bg-secondary pr-3"><fmt:message key="shop.filterByType"/></span>
            </h5>
            <div class="bg-light p-4 mb-30">
                <form id="type-filter-form">
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="typeId" value="all" id="type-all">
                        <label class="custom-control-label" for="type-all"><fmt:message key="shop.allTypes"/></label>
                    </div>
                    <c:forEach var="type" items="${types}">
                        <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                            <input type="checkbox" class="custom-control-input" name="typeId" value="${type.id}" id="type-${type.id}" 
                                   <c:if test="${not empty typeIds and fn:contains(typeIds, type.id)}">checked</c:if>>
                            <label class="custom-control-label" for="type-${type.id}">${type.name}</label>
                        </div>
                    </c:forEach>
                </form>
            </div>
        </div>
        <!-- Filter Sidebar End -->

        <!-- Shop Product Start -->
        <div class="col-lg-9 col-md-8">
            <div class="row pb-3" id="product-container">
                <!-- Product List (Using the fragment) -->
                <jsp:include page="WEB-INF/fragment/product-list.jsp" />
            </div>
        </div>
        <!-- Shop Product End -->
    </div>
</div>

<%@ include file="WEB-INF/footer.jsp" %>
<script src="${pageContext.request.contextPath}/js/pagination.js"></script>

<script>
    $(document).ready(function() {
        // Initialize product listing page functionality
        initProductListingPage();
    });
</script>
