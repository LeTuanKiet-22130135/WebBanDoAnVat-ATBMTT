<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../WEB-INF/admin/header.jsp"%>

<body>
    <div class="container-fluid p-0">
        <div class="row no-gutters">
            <!-- Sidebar Menu -->
            <nav class="col-md-3 col-lg-2 bg-dark text-light sidebar py-5">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link text-light" href="UserControl">
                            <i class="fas fa-users"></i> 
                            <fmt:message key="admin.userControl"/>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-light" href="ProductControl">
                            <i class="fas fa-boxes"></i> 
                            <fmt:message key="admin.productControl"/>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link text-light" href="#">
                            <i class="fas fa-home"></i> 
                            <fmt:message key="admin.goToHome"/>
                        </a>
                    </li>
                </ul>
            </nav>

            <!-- Main Content -->
            <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4 py-4">
                <h1 class="h2 mb-4">
                    <fmt:message key="admin.dashboard"/>
                </h1>
                <p class="lead">
                    <fmt:message key="admin.welcome"/>
                </p>

                <div class="row">
                    <!-- User Control Section -->
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0">
                                    <fmt:message key="admin.userControl"/>
                                </h5>
                            </div>
                            <div class="card-body">
                                <p>
                                    <fmt:message key="admin.manageUsers"/>
                                </p>
                                <a href="UserControl" class="btn btn-primary">
                                    <fmt:message key="admin.goToUserControl"/>
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Product List Control Section -->
                    <div class="col-md-6 mb-4">
                        <div class="card shadow-sm">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0">
                                    <fmt:message key="admin.productControl"/>
                                </h5>
                            </div>
                            <div class="card-body">
                                <p>
                                    <fmt:message key="admin.manageProducts"/>
                                </p>
                                <a href="ProductControl" class="btn btn-success">
                                    <fmt:message key="admin.goToProductControl"/>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    </div>

    <%@ include file="../WEB-INF/admin/footer.jsp"%>
</body>
