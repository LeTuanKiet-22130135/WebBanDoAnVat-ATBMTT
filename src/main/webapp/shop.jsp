<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%@ include file="WEB-INF/header.jsp"%>

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
                        <label class="custom-control-label" for="price-1">0 - 10,000 đ</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="10001-20000" id="price-2">
                        <label class="custom-control-label" for="price-2">10,000 - 20,000 đ</label>

                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="20001-30000" id="price-3">
                        <label class="custom-control-label" for="price-3">20,000 - 30,000 đ</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="30001-40000" id="price-4">
                        <label class="custom-control-label" for="price-4">30,000 - 40,000 đ</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between mb-3">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="40001-50000" id="price-5">
                        <label class="custom-control-label" for="price-5">40,000 - 50,000 đ</label>
                    </div>
                    <div class="custom-control custom-checkbox d-flex align-items-center justify-content-between">
                        <input type="checkbox" class="custom-control-input" name="priceRange" value="over50000" id="price-morethan">
                        <label class="custom-control-label" for="price-morethan"><fmt:message key="shop.morethan"/> 50,000 đ</label>
                    </div>
                </form>
            </div>
        </div>
        <!-- Filter Sidebar End -->

        <!-- Shop Product Start -->
        <div class="col-lg-9 col-md-8">
            <div class="row pb-3" id="product-container">
                <!-- Product List (Rendered by Server) -->
                <c:forEach var="product" items="${products}">
                    <div class="col-lg-4 col-md-6 col-sm-6 pb-1">
                        <div class="product-item bg-light mb-4">
                            <div class="product-img position-relative overflow-hidden">
                                <img class="img-fluid w-100" src="${product.img}" alt="${product.name}" referrerPolicy="no-referrer">
                                <div class="product-action">
                                    <a class="btn btn-outline-dark btn-square" href=""><i class="fa fa-shopping-cart"></i></a>
                                    <a class="btn btn-outline-dark btn-square" href=""><i class="far fa-heart"></i></a>
                                    <a class="btn btn-outline-dark btn-square" href=""><i class="fa fa-sync-alt"></i></a>
                                    <a class="btn btn-outline-dark btn-square" href="detail?id=${product.id}"><i class="fa fa-search"></i></a>
                                </div>
                            </div>
                            <div class="text-center py-4">
                                <a class="h6 text-decoration-none text-truncate" href="detail?id=${product.id}">${product.name}</a>
                                <div class="d-flex align-items-center justify-content-center mt-2">
                                    <h5>${product.price} đ</h5>
                                </div>
                                <div class="d-flex align-items-center justify-content-center mb-1">
                                    <small class="fa fa-star text-primary mr-1"></small>
                                    <small class="fa fa-star text-primary mr-1"></small>
                                    <small class="fa fa-star text-primary mr-1"></small>
                                    <small class="fa fa-star text-primary mr-1"></small>
                                    <small class="fa fa-star-half-alt text-primary mr-1"></small>
                                    <small>(99)</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty products}">
                    <div class="col-12">
                        <p>No products found for your search.</p>
                    </div>
                </c:if>
            </div>
        </div>
        <!-- Shop Product End -->
    </div>
</div>

<%@ include file="WEB-INF/footer.jsp" %>
<script src="js/filter.js"></script>



