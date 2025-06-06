<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!-- Iterate over products and generate product cards -->
<c:forEach var="product" items="${products}">
    <div class="col-lg-4 col-md-6 col-sm-6 pb-1">
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
                    <h6 class="text-muted ml-2"><del><fmt:formatNumber value="${product.price}" pattern="#,##0.## ₫"/></del></h6>
                </div>
                <div class="d-flex align-items-center justify-content-center mb-1">
                    <c:set var="rating" value="${productRatings[product.id] != null ? productRatings[product.id] : 0}" />
                    <c:set var="reviewCount" value="${productReviewCounts[product.id] != null ? productReviewCounts[product.id] : 0}" />

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

<!-- Message when no products are found -->
<c:if test="${empty products}">
    <div class="col-12">
        <p>No products found for your search.</p>
    </div>
</c:if>

<!-- Pagination -->
<div class="col-12">
    <nav>
        <ul class="pagination justify-content-center" id="pagination">
            <!-- Previous button -->
            <c:choose>
                <c:when test="${currentPage > 1}">
                    <li class="page-item">
                        <a class="page-link" href="javascript:void(0)" data-page="${currentPage - 1}">Previous</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="page-item disabled">
                        <a class="page-link" href="javascript:void(0)">Previous</a>
                    </li>
                </c:otherwise>
            </c:choose>

            <!-- Page numbers -->
            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <li class="page-item active">
                            <a class="page-link" href="javascript:void(0)" data-page="${i}">${i}</a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="page-item">
                            <a class="page-link" href="javascript:void(0)" data-page="${i}">${i}</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <!-- Next button -->
            <c:choose>
                <c:when test="${currentPage < totalPages}">
                    <li class="page-item">
                        <a class="page-link" href="javascript:void(0)" data-page="${currentPage + 1}">Next</a>
                    </li>
                </c:when>
                <c:otherwise>
                    <li class="page-item disabled">
                        <a class="page-link" href="javascript:void(0)">Next</a>
                    </li>
                </c:otherwise>
            </c:choose>
        </ul>
    </nav>
</div>
