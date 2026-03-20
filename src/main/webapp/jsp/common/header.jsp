<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty pageTitle ? 'TA Recruitment System' : pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>
<header class="system-header py-3 mb-4">
    <div class="container d-flex justify-content-between align-items-center flex-wrap gap-3">
        <a class="system-logo text-decoration-none" href="${pageContext.request.contextPath}/">TA Recruitment System</a>
        <c:if test="${not empty sessionScope.currentUser}">
            <div class="d-flex align-items-center gap-3">
                <span>
                    <strong>${sessionScope.currentUser.username}</strong>
                    <span class="badge text-bg-light">${sessionScope.currentUser.role}</span>
                </span>
                <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
                    <button type="submit" class="btn btn-outline-light btn-sm">Logout</button>
                </form>
            </div>
        </c:if>
    </div>
</header>
<main class="container pb-5">
<c:if test="${not empty sessionScope.flash}">
    <div class="alert alert-info">${sessionScope.flash}</div>
    <c:remove var="flash" scope="session"/>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
