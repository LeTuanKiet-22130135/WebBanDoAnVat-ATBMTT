<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ include file="WEB-INF/header.jsp"%>

<div class="container-fluid pb-5">
    <div class="row px-xl-5">
        <!-- Product Image Section -->
        <div class="col-lg-5 mb-30">
            <div class="bg-light">
                <img class="img-fluid w-100" src="${product.img}" alt="${product.name}">
            </div>
        </div>

        <!-- Product Info Section -->
        <div class="col-lg-7 h-auto mb-30">
            <div class="h-100 bg-light p-30">
                <h3>${product.name}</h3>
                <div class="d-flex mb-3">
                    <div class="text-primary mr-2">
                        <small class="fas fa-star"></small>
                        <small class="fas fa-star"></small>
                        <small class="fas fa-star"></small>
                        <small class="fas fa-star-half-alt"></small>
                        <small class="far fa-star"></small>
                    </div>
                    <small class="pt-1">(99 Reviews)</small>
                </div>
                <h3 class="font-weight-semi-bold mb-4">
                    <span id="product-price">
                        <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₫"/>
                    </span>
                </h3>
                <p class="mb-4">${product.desc}</p>

                <!-- Variant Selection -->
                <div class="mb-4">
                    <label for="variant-select"><strong>Choose Variant:</strong></label>
                    <select id="variant-select" class="form-control">
                        <c:forEach var="variant" items="${product.variants}">
                            <option value="${variant.id}" data-price="${variant.price}">
                                    ${variant.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <form action="cart" method="post" id="add-to-cart-form">
                    <div class="d-flex align-items-center mb-4 pt-2">
                        <div class="input-group quantity mr-3" style="width: 130px;">
                            <div class="input-group-btn">
                                <button type="button" class="btn btn-primary btn-minus"><i class="fa fa-minus"></i></button>
                            </div>
                            <input type="text" name="quantity" class="form-control bg-secondary border-0 text-center" value="1">
                            <div class="input-group-btn">
                                <button type="button" class="btn btn-primary btn-plus"><i class="fa fa-plus"></i></button>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary px-3">
                            <i class="fa fa-shopping-cart mr-1"></i> Add To Cart
                        </button>
                        <input type="hidden" name="productId" value="${product.id}">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="variantId" id="selected-variant-id" value="${product.variants[0].id}">
                    </div>
                </form>
                <div class="d-flex pt-2">
                    <strong class="text-dark mr-2">Share on:</strong>
                    <div class="d-inline-flex">
                        <a class="text-dark px-2" href="#"><i class="fab fa-facebook-f"></i></a>
                        <a class="text-dark px-2" href="#"><i class="fab fa-twitter"></i></a>
                        <a class="text-dark px-2" href="#"><i class="fab fa-linkedin-in"></i></a>
                        <a class="text-dark px-2" href="#"><i class="fab fa-pinterest"></i></a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Tabs for Description and Review -->
    <div class="row px-xl-5">
        <div class="col">
            <div class="bg-light p-30">
                <div class="nav nav-tabs mb-4">
                    <a class="nav-item nav-link text-dark active" data-toggle="tab" href="#tab-pane-1">Description</a>
                    <a class="nav-item nav-link text-dark" data-toggle="tab" href="#tab-pane-2">Reviews (0)</a>
                </div>
                <div class="tab-content">
                    <!-- Description Tab -->
                    <div class="tab-pane fade show active" id="tab-pane-1">
                        <h4 class="mb-3">Product Description</h4>
                        <p>${product.desc}</p>
                    </div>
                    <!-- Review Tab -->
                    <div class="tab-pane fade" id="tab-pane-2">
                        <div class="row">
                            <div class="col-md-6">
                                <h4 class="mb-4">1 review for "${product.name}"</h4>
                                <div class="media mb-4">
                                    <img src="img/user.jpg" alt="User" class="img-fluid mr-3 mt-1" style="width: 45px;">
                                    <div class="media-body">
                                        <h6>John Doe<small> - <i>01 Jan 2024</i></small></h6>
                                        <div class="text-primary mb-2">
                                            <i class="fas fa-star"></i><i class="fas fa-star"></i>
                                            <i class="fas fa-star"></i><i class="fas fa-star-half-alt"></i>
                                            <i class="far fa-star"></i>
                                        </div>
                                        <p>Great product! Highly recommend.</p>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <h4 class="mb-4">Leave a review</h4>
                                <form>
                                    <div class="form-group">
                                        <label for="message">Your Review *</label>
                                        <textarea id="message" cols="30" rows="5" class="form-control"></textarea>
                                    </div>
                                    <div class="form-group">
                                        <label for="name">Your Name *</label>
                                        <input type="text" class="form-control" id="name">
                                    </div>
                                    <div class="form-group">
                                        <label for="email">Your Email *</label>
                                        <input type="email" class="form-control" id="email">
                                    </div>
                                    <div class="form-group mb-0">
                                        <input type="submit" value="Leave Your Review" class="btn btn-primary px-3">
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

<script src="js/variant-selection.js" defer></script>
<%@ include file="WEB-INF/footer.jsp" %>
