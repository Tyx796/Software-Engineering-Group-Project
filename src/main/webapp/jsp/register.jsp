<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register"/>
<%@ include file="common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-6">
        <div class="card shadow-sm auth-card">
            <div class="card-body p-4">
                <h1 class="h3 text-center mb-3">Create account</h1>
                <form method="post" action="${pageContext.request.contextPath}/register" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label class="form-label">Role</label>
                        <select class="form-select" name="role" required>
                            <option value="APPLICANT" ${formRole == 'APPLICANT' ? 'selected' : ''}>Applicant</option>
                            <option value="ORGANISER" ${formRole == 'ORGANISER' ? 'selected' : ''}>Organiser</option>
                            <option value="ADMIN" ${formRole == 'ADMIN' ? 'selected' : ''}>Admin</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="username">Username</label>
                        <input class="form-control" type="text" id="username" name="username" required value="${formUsername}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input class="form-control" type="email" id="email" name="email" required value="${formEmail}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="password">Password</label>
                        <input class="form-control" type="password" id="password" name="password" minlength="6" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="confirmPassword">Confirm password</label>
                        <input class="form-control" type="password" id="confirmPassword" name="confirmPassword" minlength="6" required>
                    </div>
                    <button class="btn btn-primary w-100" type="submit">Register</button>
                </form>
                <p class="mt-3 mb-0 text-center">Already registered? <a href="${pageContext.request.contextPath}/login">Login</a></p>
            </div>
        </div>
    </div>
</div>
<%@ include file="common/footer.jsp" %>
