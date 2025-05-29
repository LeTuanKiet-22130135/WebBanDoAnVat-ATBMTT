<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../WEB-INF/admin/header.jsp"%>

<body>
    <div class="container mt-5">
        <h1 class="text-center">Add Product</h1>
        <form action="AddProductServlet" method="post" enctype="multipart/form-data">
            <input type="hidden" name="action" value="add">
            <div class="form-row">
                <div class="form-group col-md-6">
                    <label for="name">Product Name</label>
                    <input type="text" class="form-control" id="name" name="name" required>
                </div>
                <div class="form-group col-md-6">
                    <label for="price">Price</label>
                    <input type="number" step="0.01" class="form-control" id="price" name="price" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group col-md-6">
                    <label for="quantity">Quantity</label>
                    <input type="number" class="form-control" id="quantity" name="quantity" required>
                </div>
                <div class="form-group col-md-6">
                    <label for="imageFile">Product Image</label>
                    <input type="file" class="form-control" id="imageFile" name="imageFile" required>
                </div>
            </div>
            <div class="form-group">
                <label for="description">Description</label>
                <textarea class="form-control" id="description" name="description" rows="3"></textarea>
            </div>
            <button type="submit" class="btn btn-primary">Add Product</button>
        </form>
    </div>
</body>
