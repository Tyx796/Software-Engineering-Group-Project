<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register"/>
<c:set var="pageLayout" value="auth"/>
<%@ include file="common/header.jsp" %>
<div class="auth-grid">
    <%@ include file="common/auth_visual.jsp" %>
    <section class="auth-hero">
        <h1 class="auth-title">Create the right account.</h1>
    </section>
    <section class="auth-panel">
        <div class="auth-panel-header">
            <div class="section-kicker">New Account</div>
            <h1 class="auth-panel-title">Create account</h1>
            <p class="auth-panel-subtitle">Choose the correct role and complete the required credentials.</p>
        </div>
        <div class="auth-form-card">
            <form method="post" action="${pageContext.request.contextPath}/register" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label class="form-label">Role</label>
                    <select class="form-select" name="role" required>
                        <option value="APPLICANT" ${formRole == 'APPLICANT' ? 'selected' : ''}>Applicant</option>
                        <option value="ORGANISER" ${formRole == 'ORGANISER' ? 'selected' : ''}>Organiser</option>
                        <option value="ADMIN" ${formRole == 'ADMIN' ? 'selected' : ''}>Admin</option>
                    </select>
                    <div class="invalid-feedback">Please choose a role.</div>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="username">Username</label>
                    <input class="form-control" type="text" id="username" name="username" required value="${formUsername}">
                    <div class="invalid-feedback">Please enter a username.</div>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="email">Email</label>
                    <input class="form-control" type="email" id="email" name="email" required value="${formEmail}">
                    <div class="invalid-feedback">Please enter a valid email address.</div>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="password">Password</label>
                    <input class="form-control" type="password" id="password" name="password" minlength="6" required>
                    <div class="invalid-feedback">Password must be at least 6 characters.</div>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="confirmPassword">Confirm password</label>
                    <input class="form-control" type="password" id="confirmPassword" name="confirmPassword" minlength="6" required>
                    <div class="invalid-feedback">Please confirm your password.</div>
                </div>
                <button class="btn btn-primary w-100" type="submit">Register</button>
            </form>
            <p class="auth-footer-note mb-0">Already registered? <a href="${pageContext.request.contextPath}/login">Login</a></p>
        </div>
    </section>
</div>
<%@ include file="common/footer.jsp" %>
