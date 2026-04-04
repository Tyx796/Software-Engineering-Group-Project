<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login"/>
<%@ include file="common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-5">
        <div class="card shadow-sm auth-card">
            <div class="card-body p-4">
                <h1 class="h3 text-center mb-3">Sign in</h1>
                <form method="post" action="${pageContext.request.contextPath}/login" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input class="form-control" type="email" id="email" name="email" required value="${formEmail}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="password">Password</label>
                        <input class="form-control" type="password" id="password" name="password" required>
                    </div>
                    <button class="btn btn-primary w-100" type="submit">Login</button>
                </form>
                <p class="mt-3 mb-0 text-center">No account yet? <a href="${pageContext.request.contextPath}/register">Register</a></p>
            </div>
        </div>
    </div>
</div>
<%@ include file="common/footer.jsp" %>
