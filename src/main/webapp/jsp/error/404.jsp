<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header class="system-header py-3 mb-4">
    <div class="container">
        <a class="system-logo text-decoration-none" href="${pageContext.request.contextPath}/">TA Recruitment System</a>
    </div>
</header>
<main class="container pb-5">
    <div class="row justify-content-center">
        <div class="col-lg-6 text-center">
            <h1 class="display-1 fw-bold text-muted">404</h1>
            <h2 class="h4 mb-3">Page Not Found</h2>
            <p class="text-muted mb-4">The page you are looking for does not exist or has been moved.</p>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/">Return to Home</a>
        </div>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
