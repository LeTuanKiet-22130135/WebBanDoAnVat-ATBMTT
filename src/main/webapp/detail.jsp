<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ include file="WEB-INF/header.jsp"%>

<style>
    /* Star Rating Styles */
    .rating-group {
        display: flex;
        flex-direction: row;
        align-items: center;
    }

    .form-check-input[type="radio"] {
        display: none;
    }

    .form-check-label {
        cursor: pointer;
        font-size: 1.5rem;
        padding: 0 0.2rem;
        color: #ddd;
    }

    .form-check-input:checked ~ .form-check-label,
    .form-check-input:checked + .form-check-label {
        color: #FFD700;
    }

    .form-check-label:hover,
    .form-check-label:hover ~ .form-check-label {
        color: #FFD700;
    }

    .form-check-inline {
        margin-right: 0;
    }

    /* Success message */
    .review-success {
        background-color: #d4edda;
        color: #155724;
        padding: 10px;
        margin-bottom: 15px;
        border-radius: 4px;
    }
</style>


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
                        <c:set var="integerPart" value="${averageRating - (averageRating % 1)}" />
                        <c:set var="decimalPart" value="${averageRating % 1}" />

                        <c:forEach begin="1" end="5" var="i">
                            <c:choose>
                                <c:when test="${i <= integerPart}">
                                    <small class="fas fa-star"></small>
                                </c:when>
                                <c:when test="${i == integerPart + 1 && decimalPart >= 0.5}">
                                    <small class="fas fa-star-half-alt"></small>
                                </c:when>
                                <c:otherwise>
                                    <small class="far fa-star"></small>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                    <small class="pt-1">(${reviewCount} Reviews)</small>
                </div>
                <h3 class="font-weight-semi-bold mb-4">
                    <span id="product-price">
                        <fmt:formatNumber value="${product.price}" pattern="#,##0.## ₫"/>
                    </span>
                </h3>
                <p class="mb-4">${product.desc}</p>

                <!-- Variant Selection -->
                <div class="mb-4">
                    <label for="variant-select"><strong><fmt:message key="detail.chooseVariant" /></strong></label>
                    <select id="variant-select" class="form-control">
                        <c:forEach var="variant" items="${product.variants}">
                            <option value="${variant.id}" data-price="${variant.price}">
                                    ${variant.name}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="d-flex align-items-center mb-4 pt-2">
                    <div class="input-group quantity mr-3" style="width: 130px;">
                        <div class="input-group-btn">
                            <button type="button" class="btn btn-primary btn-minus"><i class="fa fa-minus"></i></button>
                        </div>
                        <input type="text" id="quantity" class="form-control bg-secondary border-0 text-center" value="1">
                        <div class="input-group-btn">
                            <button type="button" class="btn btn-primary btn-plus"><i class="fa fa-plus"></i></button>
                        </div>
                    </div>
                    <button type="button" id="add-to-cart-btn" class="btn btn-primary px-3">
                        <i class="fa fa-shopping-cart mr-1"></i> <fmt:message key="button.addToCart" />
                    </button>
                    <input type="hidden" id="product-id" value="${product.id}">
                    <input type="hidden" id="selected-variant-id" value="${product.variants[0].id}">
                </div>

                <div class="d-flex pt-2">
                    <strong class="text-dark mr-2"><fmt:message key="label.share" />:</strong>
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
                    <a class="nav-item nav-link text-dark active" data-toggle="tab" href="#tab-pane-1"><fmt:message key="tab.description" /></a>
                    <a class="nav-item nav-link text-dark" data-toggle="tab" href="#tab-pane-2"><fmt:message key="detail.reviews" /> (${reviewCount})</a>
                </div>
                <div class="tab-content">
                    <!-- Description Tab -->
                    <div class="tab-pane fade show active" id="tab-pane-1">
                        <h4 class="mb-3"><fmt:message key="label.productDescription" /></h4>
                        <p>${product.desc}</p>
                    </div>
                    <!-- Review Tab -->
                    <div class="tab-pane fade" id="tab-pane-2">
                        <div class="row">
                            <div class="col-md-6">
                                <h4 class="mb-4">${reviewCount} <fmt:message key="detail.reviewsFor" /> "${product.name}"</h4>

                                <c:if test="${empty reviews}">
                                    <p><fmt:message key="detail.noReviews" /></p>
                                </c:if>

                                <c:forEach var="review" items="${reviews}">
                                    <div class="media mb-4">
                                        <img src="https://placehold.co/45x45" alt="User" class="img-fluid mr-3 mt-1" style="width: 45px;">
                                        <div class="media-body">
                                            <h6>${review.username}<small> - <i>${review.date}</i></small></h6>
                                            <div class="text-primary mb-2">
                                                <c:forEach begin="1" end="5" var="i">
                                                    <c:choose>
                                                        <c:when test="${i <= review.rating}">
                                                            <i class="fas fa-star"></i>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <i class="far fa-star"></i>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </div>
                                            <p>${review.comment}</p>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                            <div class="col-md-6">
                                <h4 class="mb-4"><fmt:message key="detail.leaveReview" /></h4>

                                <c:if test="${not empty reviewError}">
                                    <div class="alert alert-danger">
                                        ${reviewError}
                                    </div>
                                </c:if>

                                <c:choose>
                                    <c:when test="${empty username}">
                                        <div class="alert alert-info">
                                            <fmt:message key="detail.pleaseLogin" /> <a href="${pageContext.request.contextPath}/login"><fmt:message key="nav.login" /></a> <fmt:message key="detail.toLeaveReview" />
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/review/add" method="post">
                                            <input type="hidden" name="productId" value="${product.id}">

                                            <div class="form-group">
                                                <label><fmt:message key="detail.yourRating" /> *</label>
                                                <div class="stars">
                                                    <div class="rating-group">
                                                        <c:forEach begin="1" end="5" var="i">
                                                            <div class="form-check form-check-inline">
                                                                <input class="form-check-input" type="radio" name="rating" id="rating${i}" value="${i}" ${i == 5 ? 'checked' : ''}>
                                                                <label class="form-check-label" for="rating${i}">
                                                                    <i class="fas fa-star"></i>
                                                                </label>
                                                            </div>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="comment"><fmt:message key="detail.yourReview" /> *</label>
                                                <textarea id="comment" name="comment" cols="30" rows="5" class="form-control" required></textarea>
                                            </div>

                                            <div class="form-group mb-0">
                                                <button type="submit" class="btn btn-primary px-3"><fmt:message key="detail.submitReview" /></button>
                                            </div>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

<!-- Include jQuery if not already included in header -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="${pageContext.request.contextPath}/js/variant-selection.js" defer></script>
<!-- Include custom cart JavaScript -->
<script src="${pageContext.request.contextPath}/js/cart.js"></script>

<!-- Hidden elements for i18n in JavaScript -->
<div id="i18n-messages" style="display: none;">
    <span id="msg-review-submitted"><fmt:message key="detail.reviewSubmitted" /></span>
</div>

<script>
    $(document).ready(function() {
        // Initialize product detail page functionality
        initProductDetailPage();

        // Initialize star rating
        initStarRating();

        // Show success message if review was submitted successfully
        if (new URLSearchParams(window.location.search).get('reviewSuccess') === 'true') {
            showReviewSuccess();
        }

        // Activate review tab if coming from review submission or there's an error
        if (window.location.search.includes('reviewSuccess=true') || window.location.search.includes('error=')) {
            $('.nav-tabs a[href="#tab-pane-2"]').tab('show');
        }
    });

    function initStarRating() {
        // Handle star rating selection
        $('.form-check-label').on('click', function() {
            const rating = $(this).prev('input').val();

            // Reset all stars
            $('.form-check-label').css('color', '#ddd');

            // Highlight selected stars
            for (let i = 1; i <= rating; i++) {
                $(`label[for="rating${i}"]`).css('color', '#FFD700');
            }
        });
    }

    function showReviewSuccess() {
        // Get the translated message from the hidden element
        const reviewSubmittedMsg = $('#msg-review-submitted').text();

        // Add success message before the review form
        const successMessage = $('<div class="review-success mb-3">' + reviewSubmittedMsg + '</div>');
        $('form[action*="/review/add"]').before(successMessage);

        // Remove the message after 5 seconds
        setTimeout(function() {
            successMessage.fadeOut('slow', function() {
                $(this).remove();
            });
        }, 5000);
    }
</script>

<%@ include file="WEB-INF/footer.jsp" %>
