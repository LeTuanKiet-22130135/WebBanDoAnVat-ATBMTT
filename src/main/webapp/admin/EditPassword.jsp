<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../WEB-INF/admin/header.jsp"%>

<div class="container mt-5">
	<div class="row justify-content-center">
		<div class="col-md-6">
			<h2 class="text-center">Edit Password</h2>
			<form action="editPassword" method="post">
				<input type="hidden" name="userId" value="${userId}">
				<div class="form-group">
					<label for="newPassword">New Password</label> <input
						type="password" class="form-control" id="newPassword"
						name="newPassword" required>
				</div>
				<button type="submit" class="btn btn-primary">Update
					Password</button>
				<a href="UserControl" class="btn btn-secondary">Cancel</a>
			</form>

			<!-- Display success or error message -->
			<c:if test="${not empty message}">
				<div class="alert alert-success mt-3">${message}</div>
			</c:if>
			<c:if test="${not empty error}">
				<div class="alert alert-danger mt-3">${error}</div>
			</c:if>
		</div>
	</div>

</div>
</body>
</html>
