<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageLayout" value="${empty pageLayout ? 'workspace' : pageLayout}"/>
<c:set var="pageSection" value="${empty pageSection ? '' : pageSection}"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty pageTitle ? 'TA Recruitment System' : pageTitle}</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@500;700;800&family=Source+Sans+3:wght@400;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body class="${pageLayout == 'auth' ? 'auth-body' : 'workspace-body'}">
<c:choose>
    <c:when test="${pageLayout == 'auth'}">
        <div class="auth-shell">
            <main class="auth-main">
                <c:if test="${not empty sessionScope.flash}">
                    <div class="px-4 pt-4">
                        <div class="alert alert-info mb-0">${sessionScope.flash}</div>
                    </div>
                    <c:remove var="flash" scope="session"/>
                </c:if>
                <c:if test="${not empty error}">
                    <div class="px-4 pt-4">
                        <div class="alert alert-danger mb-0">${error}</div>
                    </div>
                </c:if>
    </c:when>
    <c:otherwise>
        <div class="app-shell app-shell-body">
            <header class="workspace-header">
                <div class="workspace-header-bar">
                    <div class="workspace-brand">
                        <a class="brand-lockup text-decoration-none" href="${pageContext.request.contextPath}/">
                            <div class="brand-mark-shell" aria-hidden="true">
                                <div class="brand-mark">
                                    <span class="brand-mark-monogram">TA</span>
                                    <span class="brand-mark-ornament"></span>
                                </div>
                            </div>
                            <div class="brand-copy">
                                <div class="brand-topline">
                                    <div class="brand-eyebrow">Campus TA Workspace</div>
                                    <span class="brand-divider"></span>
                                    <span class="brand-note">Editorial Edition</span>
                                </div>
                                <span class="system-logo">TA Recruitment System</span>
                                <p class="brand-description">Browse roles, review candidates, and manage TA hiring in one calm workspace.</p>
                            </div>
                        </a>
                    </div>
                    <c:if test="${not empty sessionScope.currentUser}">
                        <div class="workspace-user">
                            <div class="user-copy">
                                <span class="user-name">${sessionScope.currentUser.username}</span>
                                <span class="role-badge">${sessionScope.currentUser.role}</span>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
                                <button type="submit" class="btn btn-outline-secondary btn-sm">Logout</button>
                            </form>
                        </div>
                    </c:if>
                </div>
                <c:if test="${not empty sessionScope.currentUser}">
                    <%@ include file="navigation.jsp" %>
                </c:if>
            </header>
            <div class="shell-body">
                <main class="shell-main">
                    <div class="shell-frame">
                        <c:if test="${not empty sessionScope.flash}">
                            <div class="alert alert-info">${sessionScope.flash}</div>
                            <c:remove var="flash" scope="session"/>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>
    </c:otherwise>
</c:choose>
