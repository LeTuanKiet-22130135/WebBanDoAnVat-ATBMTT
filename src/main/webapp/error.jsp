<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Error</title>
    <link href="css/style.css" rel="stylesheet">
    <style>
        .error-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        .error-code {
            font-size: 72px;
            color: #dc3545;
            margin-bottom: 20px;
        }
        .error-message {
            font-size: 24px;
            color: #343a40;
            margin-bottom: 30px;
        }
        .back-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #ffc107;
            color: #212529;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: background-color 0.3s;
        }
        .back-button:hover {
            background-color: #e0a800;
        }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/header.jsp" />

    <div class="container-fluid">
        <div class="error-container">
            <div class="error-code">
                <%= request.getAttribute("errorCode") != null ? request.getAttribute("errorCode") : "Error" %>
            </div>
            <div class="error-message">
                <%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "An error occurred while processing your request." %>
            </div>
            <% if(request.getAttribute("code") != null && request.getAttribute("codeMessage") != null) { %>
            <div class="error-details" style="margin-bottom: 20px; font-size: 18px; color: #6c757d;">
                <p><strong>Error Code:</strong> <%= request.getAttribute("code") %></p>
                <p><strong>Error Message:</strong> <%= request.getAttribute("codeMessage") %></p>
                <% if(request.getAttribute("apiMessage") != null) { %>
                <p><strong>API Message:</strong> <%= request.getAttribute("apiMessage") %></p>
                <% } %>
            </div>
            <% } %>
            <a href="index" class="back-button">Back to Home</a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>
