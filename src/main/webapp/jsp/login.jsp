<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login"/>
<c:set var="pageLayout" value="auth"/>
<%@ include file="common/header.jsp" %>
<div class="auth-grid">
    <%@ include file="common/auth_visual.jsp" %>
    <section class="auth-hero">
        <h1 class="auth-title">Coordinate TA hiring.</h1>
    </section>
    <section class="auth-panel">
        <div class="auth-panel-header">
            <div class="section-kicker">Account Access</div>
            <h1 class="auth-panel-title">Sign in</h1>
            <p class="auth-panel-subtitle">Use your existing account to continue in the recruitment workspace.</p>
        </div>
        <div class="auth-form-card">
            <form method="post" action="${pageContext.request.contextPath}/login" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label class="form-label" for="email">Email</label>
                    <input class="form-control" type="email" id="email" name="email" required value="${formEmail}">
                    <div class="invalid-feedback">Please enter your email address.</div>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="password">Password</label>
                    <input class="form-control" type="password" id="password" name="password" required>
                    <div class="invalid-feedback">Please enter your password.</div>
                </div>
                <button class="btn btn-primary w-100" type="submit">Login</button>
            </form>
            <p class="auth-footer-note mb-0">No account yet? <a href="${pageContext.request.contextPath}/register">Register</a></p>
        </div>
    </section>
</div>
<%@ include file="common/footer.jsp" %>
