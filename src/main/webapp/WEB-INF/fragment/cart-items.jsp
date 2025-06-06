<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>

<!-- Cart Items Table -->
<table class="table table-light table-borderless table-hover text-center mb-0">
    <thead class="thead-dark">
        <tr>
            <th><fmt:message key="cart.products" /></th>
            <th><fmt:message key="cart.price" /></th>
            <th><fmt:message key="cart.quantity" /></th>
            <th><fmt:message key="cart.total" /></th>
            <th><fmt:message key="cart.remove" /></th>
        </tr>
    </thead>
    <tbody class="align-middle">
        <!-- Loop through cart items -->
        <c:forEach var="item" items="${cart.items}">
            <tr>
                <td class="align-middle"><img src="${item.productImageUrl}"
                    alt="${item.productName}" style="width: 50px;">
                    ${item.productName}</td>
                <td class="align-middle"><fmt:formatNumber value="${item.price}" pattern="#,##0.## ₫"/></td>
                <td class="align-middle">
                    <!-- Quantity update form -->
                    <div class="input-group quantity mx-auto" style="width: 100px;">
                        <div class="input-group-btn">
                            <button type="button" data-action="decrease" data-product-id="${item.productId}" 
                                data-variant-id="${item.variantId}" 
                                class="btn btn-sm btn-primary btn-minus cart-update-btn"
                                <c:if test="${item.quantity <= 1}">disabled</c:if>>
                                <i class="fa fa-minus"></i>
                            </button>
                        </div>
                        <input type="text"
                            class="form-control form-control-sm bg-secondary border-0 text-center"
                            value="${item.quantity}" readonly>
                        <div class="input-group-btn">
                            <button type="button" data-action="increase" data-product-id="${item.productId}" 
                                data-variant-id="${item.variantId}" 
                                class="btn btn-sm btn-primary btn-plus cart-update-btn">
                                <i class="fa fa-plus"></i>
                            </button>
                        </div>
                    </div>
                </td>
                <td class="align-middle"><fmt:formatNumber value="${item.totalPrice}" pattern="#,##0.## ₫"/></td>
                <td class="align-middle">
                    <!-- Remove item button -->
                    <button type="button" data-product-id="${item.productId}" 
                        data-variant-id="${item.variantId}" 
                        class="btn btn-sm btn-danger cart-remove-btn">
                        <i class="fa fa-times"></i>
                    </button>
                </td>
            </tr>
        </c:forEach>

        <!-- If the cart is empty -->
        <c:if test="${empty cart.items}">
            <tr>
                <td colspan="5" class="text-center"><fmt:message key="cart.emptyCart" /></td>
            </tr>
        </c:if>
    </tbody>
</table>
