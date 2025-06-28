<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
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
                    <a href="profile" class="list-group-item list-group-item-action">
                        <fmt:message key="footer.profile" />
                    </a>
                    <a href="pubkey" class="list-group-item list-group-item-action active">
                        Public Key Management
                    </a>
                    <a href="orderhistory" class="list-group-item list-group-item-action">
                        <fmt:message key="footer.orderHistory" />
                    </a>
                    <c:if test="${user.hashedPassword != null}">
                        <a href="changepassword" class="list-group-item list-group-item-action"><fmt:message key="footer.changePassword" /></a>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Public Key Management Section -->
        <div class="col-lg-9">
            <div class="bg-light p-30 mb-5">
                <h5 class="section-title position-relative text-uppercase mb-3">
                    <span class="bg-secondary pr-3">Public Key Management</span>
                </h5>

                <!-- Display success or error messages -->
                <c:if test="${not empty success}">
                    <div class="alert alert-success" role="alert">
                        ${success}
                    </div>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>

                <!-- Public Key Information -->
                <div class="mb-4">
                    <h6 class="mb-3">Public Key Information</h6>
                    <c:choose>
                        <c:when test="${pubkey != null}">
                            <div class="card">
                                <div class="card-body">
                                    <p><strong>Status:</strong> 
                                        <c:choose>
                                            <c:when test="${pubkey.available}">
                                                <span class="badge badge-success">Active</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-secondary">Inactive</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    <p><strong>Key Size:</strong> 1024 bits (RSA)</p>

                                    <!-- Actions -->
                                    <div class="mt-3">
                                        <form action="pubkey" method="post" class="d-inline">
                                            <input type="hidden" name="action" value="toggle">
                                            <button type="submit" class="btn btn-sm btn-warning">
                                                <c:choose>
                                                    <c:when test="${pubkey.available}">Disable Key</c:when>
                                                    <c:otherwise>Enable Key</c:otherwise>
                                                </c:choose>
                                            </button>
                                        </form>
                                        <form action="pubkey" method="post" class="d-inline ml-2">
                                            <input type="hidden" name="action" value="delete">
                                            <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete your public key?')">Delete Key</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-info" role="alert">
                                You don't have a public key registered yet. Upload one below.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Upload Public Key Form -->
                <div>
                    <h6 class="mb-3">Upload Public Key</h6>
                    <form action="pubkey" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="upload">
                        <div class="form-group">
                            <label for="pubkeyFile">Select .pub file (RSA 1024-bit)</label>
                            <input type="file" class="form-control-file" id="pubkeyFile" name="pubkeyFile" required>
                            <small class="form-text text-muted">
                                Upload a .pub file containing your RSA public key (1024 bits).
                            </small>
                        </div>
                        <button type="submit" class="btn btn-primary">Upload Key</button>
                    </form>
                </div>

                <!-- Change Password Section -->
                <c:if test="${user.hashedPassword != null}">
                    <div class="mt-5">
                        <h5 class="section-title position-relative text-uppercase mb-3">
                            <span class="bg-secondary pr-3"><fmt:message key="footer.changePassword" /></span>
                        </h5>
                        <form action="changepassword" method="post" onsubmit="return validateForm()">
                            <div class="form-group">
                                <label><fmt:message key="changepassword.currentPassword" /></label>
                                <input class="form-control" type="password" name="currentPassword" required>
                            </div>
                            <div class="form-group">
                                <label><fmt:message key="changepassword.newPassword" /></label>
                                <input class="form-control" type="password" name="newPassword" required>
                            </div>
                            <div class="form-group">
                                <label><fmt:message key="changepassword.repeatNewPassword" /></label>
                                <input class="form-control" type="password" name="repeatNewPassword" required>
                            </div>
                            <button type="submit" class="btn btn-primary mt-3"><fmt:message key="button.changePassword" /></button>
                        </form>
                    </div>
                </c:if>

                <!-- Information about public keys -->
                <div class="mt-4">
                    <div class="card">
                        <div class="card-header bg-secondary text-white">
                            About Public Key Registration
                        </div>
                        <div class="card-body">
                            <p>Public key registration allows you to securely authenticate with our system. We use RSA algorithm with 1024-bit keys.</p>
                            <p>To generate an RSA key pair:</p>
                            <ol>
                                <li>Use OpenSSL or a similar tool to generate your key pair</li>
                                <li>Keep your private key secure and never share it</li>
                                <li>Upload only your public key (.pub file) to our system</li>
                            </ol>
                            <p><strong>Example OpenSSL command:</strong><br>
                            <code>openssl genrsa -out private.pem 1024</code><br>
                            <code>openssl rsa -in private.pem -pubout -out public.pub</code></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="WEB-INF/footer.jsp" %>
