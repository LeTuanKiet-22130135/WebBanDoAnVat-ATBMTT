<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="${cookie.lang.value != null ? cookie.lang.value : 'en'}" />
<fmt:setBundle basename="messages" />
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<title>ShopAnVat - Admin Dashboard</title>
<meta content="width=device-width, initial-scale=1.0" name="viewport">
<meta content="Free HTML Templates" name="keywords">
<meta content="Free HTML Templates" name="description">

<!-- Favicon -->
<link href="img/favicon.ico" rel="icon">

<!-- Google Web Fonts -->
<link rel="preconnect" href="https://fonts.gstatic.com">
<link
	href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap"
	rel="stylesheet">

<!-- Font Awesome -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.10.0/css/all.min.css"
	rel="stylesheet">

<!-- Libraries Stylesheet -->
<link href="../lib/animate/animate.min.css" rel="stylesheet">
<link href="../lib/owlcarousel/assets/owl.carousel.min.css"
	rel="stylesheet">

<!-- Customized Bootstrap Stylesheet -->
<link href="../css/style.css" rel="stylesheet">

<!-- Custom Styles -->
<style>
.account-login {
	display: flex;
	justify-content: flex-end;
	align-items: center;
	margin-left: auto;
}

.account-login a:hover, .account-login .dropdown-item:hover {
	color: #555 !important; /* Slightly lighter color on hover */
}

</style>
</head>

<body>
	<!-- Topbar Start -->
	<div class="container-fluid">
		<div class="row align-items-center bg-light py-3 px-xl-5">
			<div class="col-lg-4">
				<a href="AdminIndex.jsp" class="text-decoration-none"> <span
					class="h1 text-uppercase text-primary bg-dark px-2">Shop</span> <span
					class="h1 text-uppercase text-dark bg-primary px-2 ml-n1">AnVat</span>
				</a>
			</div>
			<div class="col-lg-8 account-login">
				<div class="nav-item dropdown">
					<c:choose>
						<c:when test="${empty pageContext.request.remoteUser}">
							<a href="login" class="nav-link" style="color: white;"> <i
								class="fa fa-user"></i> Login
							</a>
						</c:when>
						<c:otherwise>
							<a href="#" class="nav-link dropdown-toggle"
								data-toggle="dropdown" style="color: black;"> <i class="fa fa-user text-primary"></i>
								${pageContext.request.remoteUser}
							</a>
							<div class="dropdown-menu dropdown-menu-right">
							 	<a href="adminlogout" class="dropdown-item">Logout</a>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>
		</div>
	</div>
	<!-- Topbar End -->