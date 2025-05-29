<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../WEB-INF/admin/header.jsp"%>

<body>
	<div class="container mt-5">
		<h1 class="text-center">Update Product</h1>
		<form action="UpdateProductServlet" method="post"
			enctype="multipart/form-data">
			<input type="hidden" name="id" value="${product.id}"> <input
				type="hidden" name="currentImageUrl" value="${product.imageUrl}">
			<div class="form-row">
				<div class="form-group col-md-6">
					<label for="name">Product Name</label> <input type="text"
						class="form-control" id="name" name="name" value="${product.name}"
						required>
				</div>
				<div class="form-group col-md-6">
					<label for="price">Price</label> <input type="number" step="0.01"
						class="form-control" id="price" name="price"
						value="${product.price}" required>
				</div>
			</div>
			<div class="form-row">
				<div class="form-group col-md-6">
					<label for="quantity">Quantity</label> <input type="number"
						class="form-control" id="quantity" name="quantity"
						value="${product.quantity}" required>
				</div>
				<div class="form-group col-md-6">
					<label for="imageFile">Product Image</label> <input type="file"
						class="form-control" id="imageFile" name="imageFile"> <small
						class="form-text text-muted">Current Image: <img
						src="${pageContext.request.contextPath}/imglocation/${product.imageUrl}"
						alt="Product Image" style="max-width: 100px;">
					</small>
				</div>

			</div>
			<div class="form-group">
				<label for="description">Description</label>
				<textarea class="form-control" id="description" name="description"
					rows="3">${product.description}</textarea>
			</div>
			<button type="submit" class="btn btn-primary">Update Product</button>
			<a href="ProductControl" class="btn btn-secondary">Cancel</a>
		</form>
	</div>
</body>
</body>
</html>
