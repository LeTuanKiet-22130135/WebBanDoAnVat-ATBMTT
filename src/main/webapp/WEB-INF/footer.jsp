<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="${cookie.lang.value != null ? cookie.lang.value : 'en'}" />
<fmt:setBundle basename="messages" />
<!-- Footer Start -->
<div class="container-fluid bg-dark text-secondary mt-5 pt-5">
    <div class="row px-xl-5 pt-5">
        <div class="col-lg-4 col-md-12 mb-5 pr-3 pr-xl-5">
            <h5 class="text-secondary text-uppercase mb-4">
                <fmt:message key="footer.getInTouch" />
            </h5>
            <p class="mb-2"><i class="fa fa-map-marker-alt text-primary mr-3"></i>123 Street, New York, USA</p>
            <p class="mb-2"><i class="fa fa-envelope text-primary mr-3"></i>info@example.com</p>
            <p class="mb-0"><i class="fa fa-phone-alt text-primary mr-3"></i>+012 345 67890</p>
        </div>
        <div class="col-lg-8 col-md-12">
            <div class="row">
                <div class="col-md-4 mb-5">
                    <h5 class="text-secondary text-uppercase mb-4">
                        <fmt:message key="footer.quickShop" />
                    </h5>
                    <div class="d-flex flex-column justify-content-start">
                        <a class="text-secondary mb-2" href="index"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.home" /></a>
                        <a class="text-secondary mb-2" href="shop"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.shop" /></a>
                        <a class="text-secondary mb-2" href="cart"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.cart" /></a>
                        <a class="text-secondary mb-2" href="checkout"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.checkout" /></a>
                        <a class="text-secondary" href="contact.jsp"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.contact" /></a>
                    </div>
                </div>
                <div class="col-md-4 mb-5">
                    <h5 class="text-secondary text-uppercase mb-4">
                        <fmt:message key="footer.myAccount" />
                    </h5>
                    <div class="d-flex flex-column justify-content-start">
                        <a class="text-secondary mb-2" href="profile"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.profile" /></a>
                        <a class="text-secondary mb-2" href="orderhistory"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.orderHistory" /></a>
                        <a class="text-secondary mb-2" href="changepassword"><i class="fa fa-angle-right mr-2"></i><fmt:message key="footer.changePassword" /></a>
                    </div>
                </div>
                <div class="col-md-4 mb-5">
                    <h5 class="text-secondary text-uppercase mb-4">
                        <fmt:message key="footer.newsletter" />
                    </h5>
                    <p><fmt:message key="footer.updateInfo" /></p>
                    <form action="">
                        <div class="input-group">
                            <input type="text" class="form-control" placeholder="<fmt:message key='footer.emailPlaceholder' />">
                            <div class="input-group-append">
                                <button class="btn btn-primary"><fmt:message key="footer.signUp" /></button>
                            </div>
                        </div>
                    </form>
                    <h6 class="text-secondary text-uppercase mt-4 mb-3">
                        <fmt:message key="footer.followUs" />
                    </h6>
                    <div class="d-flex">
                        <a class="btn btn-primary btn-square mr-2" href="https://x.com/"><i class="fab fa-twitter"></i></a>
                        <a class="btn btn-primary btn-square mr-2" href="https://www.facebook.com/"><i class="fab fa-facebook-f"></i></a>
                        <a class="btn btn-primary btn-square mr-2" href="https://www.linkedin.com/"><i class="fab fa-linkedin-in"></i></a>
                        <a class="btn btn-primary btn-square" href="https://www.instagram.com/"><i class="fab fa-instagram"></i></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="row border-top mx-xl-5 py-4" style="border-color: rgba(256, 256, 256, .1) !important;">
        <div class="col-md-6 px-xl-0">
            <p class="mb-md-0 text-center text-md-left text-secondary">
                &copy; <a class="text-primary" href="#">Domain</a>. <fmt:message key="footer.allRightsReserved" />
                <fmt:message key="footer.designedBy" /> <a class="text-primary" href="#">Le Tuan Kiet</a>
            </p>
        </div>
        <div class="col-md-6 px-xl-0 text-center text-md-right">
            <img class="img-fluid" src="./img/payments.png" alt="">
        </div>
    </div>
</div>
<!-- Footer End -->


<!-- Back to Top -->
<a href="#" class="btn btn-primary back-to-top"><i class="fa fa-angle-double-up"></i></a>


<!-- JavaScript Libraries -->
<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js"></script>
<script src="./lib/easing/easing.min.js"></script>
<script src="./lib/owlcarousel/owl.carousel.min.js"></script>

<!-- Contact Javascript File -->
<script src="./mail/jqBootstrapValidation.min.js"></script>
<script src="./mail/contact.js"></script>

<!-- Template Javascript -->
<script src="./js/main.js"></script>
</body>

</html>
