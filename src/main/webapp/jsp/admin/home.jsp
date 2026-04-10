<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Home"/>
<%@ include file="../common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h3 mb-3">Admin Home</h1>
                <p class="text-muted mb-4">
                    Admin accounts can now sign in and access a dedicated landing page. Workload and reporting
                    functions remain part of Iteration 4.
                </p>
                <div class="alert alert-info mb-0">
                    This page is the admin entry point for the current codebase. Iteration 4 will expand it with
                    workload management and reporting features.
                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
