<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="${cookie.lang.value != null ? cookie.lang.value : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html lang="${cookie.lang.value != null ? cookie.lang.value : 'en'}">

<head>
    <meta charset="utf-8">
    <title>ShopAnVat</title>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta content="Free HTML Templates" name="keywords">
    <meta content="Free HTML Templates" name="description">

    <!-- Favicon -->
    <link href="img/favicon.ico" rel="icon">

    <!-- Google Web Fonts -->
    <link rel="preconnect" href="https://fonts.gstatic.com">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">

    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css" rel="stylesheet">

    <!-- Libraries Stylesheet -->
    <link href="./lib/animate/animate.min.css" rel="stylesheet">
    <link href="./lib/owlcarousel/assets/owl.carousel.min.css" rel="stylesheet">

    <!-- Customized Bootstrap Stylesheet -->
    <link href="./css/style.css" rel="stylesheet">
</head>

<body>
    <!-- Topbar Start -->
    <div class="container-fluid">
        <div class="row bg-secondary py-1 px-xl-5">
            <div class="col-lg-6 text-center text-lg-right">
                <div class="d-inline-flex align-items-center d-block d-lg-none">
                    <a href="" class="btn px-0 ml-2">
                        <i class="fas fa-heart text-dark"></i>
                        <span class="badge text-dark border border-dark rounded-circle" style="padding-bottom: 2px;">0</span>
                    </a>
                    <a href="" class="btn px-0 ml-2">
                        <i class="fas fa-shopping-cart text-dark"></i>
                        <span class="badge text-dark border border-dark rounded-circle" style="padding-bottom: 2px;">0</span>
                    </a>
                </div>
            </div>
        </div>
        <div class="row align-items-center bg-light py-3 px-xl-5 d-none d-lg-flex">
            <div class="col-lg-4">
                <a href="index" class="text-decoration-none">
                    <span class="h1 text-uppercase text-primary bg-dark px-2">Shop</span>
                    <span class="h1 text-uppercase text-dark bg-primary px-2 ml-n1">AnVat</span>
                </a>
            </div>
            <div class="col-lg-4 col-6 text-left">
                <form action="shop" method="get">
                    <div class="input-group">
                        <input type="text" class="form-control" name="query" placeholder="<fmt:message key='placeholder.search' />" value="${param.query}">
                        <div class="input-group-append">
                            <button type="submit" class="input-group-text bg-transparent text-primary">
                                <i class="fa fa-search"></i>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
            <div class="col-lg-4 col-6 text-right">
                <div class="dropdown">
                    <button class="btn btn-secondary dropdown-toggle" type="button" id="languageDropdown" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <c:choose>
                            <c:when test="${cookie.lang.value == 'en' || empty cookie.lang}"><fmt:message key="label.language.english" /></c:when>
                            <c:when test="${cookie.lang.value == 'vi'}"><fmt:message key="label.language.vietnamese" /></c:when>
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
    <!-- Topbar End -->

    <!-- Navbar Start -->
    <div class="container-fluid bg-dark mb-30">
        <div class="row px-xl-5">
            <div class="col-lg-12">
                <nav class="navbar navbar-expand-lg bg-dark navbar-dark py-3 py-lg-0 px-0">
                    <a href="index" class="text-decoration-none d-block d-lg-none">
                        <span class="h1 text-uppercase text-dark bg-light px-2"><fmt:message key="title.multi" /></span>
                        <span class="h1 text-uppercase text-light bg-primary px-2 ml-n1"><fmt:message key="title.shop" /></span>
                    </a>
                    <button type="button" class="navbar-toggler" data-toggle="collapse" data-target="#navbarCollapse">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse justify-content-between" id="navbarCollapse">
                        <div class="navbar-nav mr-auto py-0">
                            <a href="index" class="nav-item nav-link active"><fmt:message key="nav.home" /></a>
                            <a href="shop" class="nav-item nav-link"><fmt:message key="nav.shop" /></a>
                            <a href="contact.jsp" class="nav-item nav-link"><fmt:message key="nav.contact" /></a>
                        </div>
                        <!-- Account and Cart Section -->
                        <div class="d-flex align-items-center">
                            <a href="cart" class="btn px-0 mr-3">
                                <i class="fas fa-shopping-cart text-primary"></i>
                            </a>
                            <div class="nav-item dropdown">
                                <c:choose>
                                    <c:when test="${empty sessionScope.username}">
                                        <a href="login" class="nav-link" style="color: white;">
                                            <i class="fa fa-user"></i> <fmt:message key="nav.login" />
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown">
                                            <i class="fa fa-user text-primary"></i> ${sessionScope.username}
                                        </a>
                                        <div class="dropdown-menu dropdown-menu-right">
                                            <a href="profile" class="dropdown-item"><fmt:message key="nav.profile" /></a>
                                            <a href="logout" class="dropdown-item"><fmt:message key="nav.logout" /></a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </nav>
            </div>
        </div>
    </div>
    <!-- Navbar End -->
</body>
