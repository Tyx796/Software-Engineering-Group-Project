<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Settings"/>
<%@ include file="../common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
            <div>
                <h1 class="h3 mb-1">Admin Settings</h1>
                <p class="text-muted mb-0">Configure the global applicant application limit.</p>
            </div>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/home">Back to admin home</a>
        </div>
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <form method="post" action="${pageContext.request.contextPath}/admin/settings" class="needs-validation" novalidate>
                    <div class="mb-3">
                        <label class="form-label" for="defaultApplicantApplicationLimit">Default applicant application limit</label>
                        <input
                                class="form-control"
                                id="defaultApplicantApplicationLimit"
                                type="number"
                                min="1"
                                name="defaultApplicantApplicationLimit"
                                value="${defaultApplicantApplicationLimit}"
                                required>
                        <div class="invalid-feedback">Default applicant application limit must be greater than zero.</div>
                        <div class="form-text">
                            This default is used unless a specific applicant override has been configured.
                        </div>
                    </div>
                    <button class="btn btn-primary" type="submit">Save Settings</button>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>