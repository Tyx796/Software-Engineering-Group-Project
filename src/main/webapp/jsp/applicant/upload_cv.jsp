<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Upload CV"/>
<%@ include file="../common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <h1 class="h3 mb-3">Upload CV</h1>
                <p class="text-muted">Accepted formats: PDF, DOC, DOCX. Uploading a new file replaces the current one.</p>
                <form method="post" action="${pageContext.request.contextPath}/applicant/cv" enctype="multipart/form-data" class="needs-validation" novalidate>
                    <input class="form-control mb-3" type="file" name="cv" accept=".pdf,.doc,.docx" required>
                    <button class="btn btn-primary" type="submit">Upload CV</button>
                    <a class="btn btn-outline-secondary ms-2" href="${pageContext.request.contextPath}/applicant/profile">Back to profile</a>
                </form>
                <c:if test="${not empty profile.cvFileName}">
                    <div class="alert alert-light border mt-4 mb-0 d-flex justify-content-between align-items-center">
                        <span><strong>Current file:</strong> ${profile.cvFileName}</span>
                        <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/applicant/cv/download">View / Download</a>
                    </div>
                </c:if>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
