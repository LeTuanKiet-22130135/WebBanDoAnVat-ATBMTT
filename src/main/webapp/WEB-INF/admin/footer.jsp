<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!-- Footer Start -->
<div class="row no-gutters" style="min-height: 100vh;">
    <div class="col-md-3 col-lg-2 bg-dark text-light sidebar py-5"></div>
    <div role="main" class="col-md-9 ml-sm-auto col-lg-10 d-flex flex-column">
        <!-- Main content fills space above footer -->
        <div class="flex-grow-1"></div>
        <!-- Footer -->
        <div class="bg-dark text-secondary" style="margin: 0px !important;">
            <div class="row border-top mx-xl-5 py-4">
                <div class="col-md-6 px-xl-0">
                    <p class="mb-md-0 text-center text-md-left text-secondary">
                        &copy; <a class="text-primary" href="#">Domain</a>. <fmt:message key="footer.allRights" />.
                        <fmt:message key="footer.designedBy" /> <a class="text-primary" href="https://htmlcodex.com">Le Tuan Kiet</a>.
                    </p>
                </div>
                <!-- Language Switcher -->
                <div class="col-md-6 px-xl-0 text-center text-md-right">
                    <div class="dropdown d-inline">
                        <button class="btn btn-secondary dropdown-toggle" type="button" id="languageDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                            <c:choose>
                                <c:when test="${cookie.lang.value == 'en' or empty cookie.lang}">English</c:when>
                                <c:when test="${cookie.lang.value == 'vi'}">Tiếng Việt</c:when>
                            </c:choose>
                        </button>
                        <div class="dropdown-menu" aria-labelledby="languageDropdown">
                            <a class="dropdown-item ${cookie.lang.value == 'en' || empty cookie.lang ? 'active' : ''}" href="setLanguage?lang=en"><fmt:message key="label.language.english" /></a>
                        	<a class="dropdown-item ${cookie.lang.value == 'vi' ? 'active' : ''}" href="setLanguage?lang=vi"><fmt:message key="label.language.vietnamese" /></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- Footer End -->

<!-- Back to Top -->
<a href="#" class="btn btn-primary back-to-top"><i class="fa fa-angle-double-up"></i></a>

<!-- JavaScript Libraries -->
<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.bundle.min.js"></script>
<script src="../lib/easing/easing.min.js"></script>
<script src="../lib/owlcarousel/owl.carousel.min.js"></script>

<!-- Contact Javascript File -->
<script src="../mail/jqBootstrapValidation.min.js"></script>
<script src="../mail/contact.js"></script>

<!-- Template Javascript -->
<script src="../js/main.js"></script>
</body>
</html>
