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
                    <a href="profile" class="list-group-item list-group-item-action active">
                        Profile
                    </a>
                    <a href="orderhistory" class="list-group-item list-group-item-action">
                        Order History
                    </a>
                    <a href="changepassword" class="list-group-item list-group-item-action">Change Password</a>
                </div>
            </div>
        </div>

        <!-- Profile Section -->
        <div class="col-lg-9">
            <div class="bg-light p-30 mb-5">
                <h5 class="section-title position-relative text-uppercase mb-3">
                    <span class="bg-secondary pr-3">Profile Information</span>
                </h5>
                <form action="profile" method="post" >
                    <div class="row">
                        <div class="col-md-6 form-group">
                            <label>First Name</label>
                            <input class="form-control" type="text" name="firstName" value="${profile.firstName}" required>
                        </div>
                        <div class="col-md-6 form-group">
                            <label>Last Name</label>
                            <input class="form-control" type="text" name="lastName" value="${profile.lastName}" required>
                        </div>
                        <div class="col-md-6 form-group">
                            <label>E-mail</label>
                            <input class="form-control" type="email" name="email" value="${profile.email}" required>
                        </div>
                        <div class="col-md-6 form-group">
                            <label>Mobile No</label>
                            <input class="form-control" type="text" name="mobileNo" value="${profile.mobileNo}">
                        </div>
                        <div class="col-md-6 form-group">
                            <label>Address Line 1</label>
                            <input class="form-control" type="text" name="addressLine1" value="${profile.addressLine1}">
                        </div>
                        <div class="col-md-6 form-group">
                            <label>Address Line 2</label>
                            <input class="form-control" type="text" name="addressLine2" value="${profile.addressLine2}">
                        </div>
                        <div class="col-md-6 form-group">
                            <label>Country</label>
                            <input class="form-control" type="text" name="country" value="${profile.country}">
                        </div>
                        <div class="col-md-6 form-group">
                            <label>City</label>
                            <input class="form-control" type="text" name="city" value="${profile.city}">
                        </div>
                        <div class="col-md-6 form-group">
                            <label>State</label>
                            <input class="form-control" type="text" name="state" value="${profile.state}">
                        </div>
                        <div class="col-md-6 form-group">
                            <label>ZIP Code</label>
                            <input class="form-control" type="text" name="zipCode" value="${profile.zipCode}">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary mt-3">Update Profile</button>
                </form>
            </div>
        </div>
    </div>
</div>

<%@ include file="WEB-INF/footer.jsp" %>
