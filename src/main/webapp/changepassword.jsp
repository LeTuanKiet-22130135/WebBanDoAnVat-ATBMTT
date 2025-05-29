<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
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
                    <a href="profile" class="list-group-item list-group-item-action">Profile</a>
                    <a href="orderhistory" class="list-group-item list-group-item-action">Order History</a>
                    <a href="changepassword" class="list-group-item list-group-item-action active">Change Password</a>
                </div>
            </div>
        </div>

        <!-- Change Password Section -->
        <div class="col-lg-9">
            <div class="bg-light p-30 mb-5">
                <h5 class="section-title position-relative text-uppercase mb-3">
                    <span class="bg-secondary pr-3">Change Password</span>
                </h5>
                <form action="changepassword" method="post" onsubmit="return validateForm()">
                    <div class="form-group">
                        <label>Current Password</label>
                        <input class="form-control" type="password" name="currentPassword" required>
                    </div>
                    <div class="form-group">
                        <label>New Password</label>
                        <input class="form-control" type="password" name="newPassword" required>
                    </div>
                    <div class="form-group">
                        <label>Repeat New Password</label>
                        <input class="form-control" type="password" name="repeatNewPassword" required>
                    </div>
                    <button type="submit" class="btn btn-primary mt-3">Change Password</button>
                </form>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger mt-3">${errorMessage}</div>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success mt-3">${successMessage}</div>
                </c:if>
            </div>
        </div>
    </div>
</div>

<%@ include file="WEB-INF/footer.jsp" %>
