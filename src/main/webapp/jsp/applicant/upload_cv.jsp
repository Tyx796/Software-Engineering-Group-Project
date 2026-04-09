<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Upload CV"/>
<%@ include file="../common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-3">
                    <div>
                        <h1 class="h3 mb-1">Manage CV</h1>
                        <p class="text-muted mb-0">Accepted formats: PDF, DOC, DOCX. Uploading a new file replaces the current one.</p>
                    </div>
                    <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/applicant/applications">My applications</a>
                </div>
                <c:choose>
                    <c:when test="${hasUploadedCv}">
                        <div class="alert alert-light border d-flex justify-content-between align-items-center flex-wrap gap-3">
                            <div>
                                <div class="small text-muted">Current CV</div>
                                <strong>${currentCvFileName}</strong>
                            </div>
                            <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/applicant/cv/download">View / Download</a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning">No CV uploaded yet. Upload one before applying for jobs.</div>
                    </c:otherwise>
                </c:choose>
                <form method="post" action="${pageContext.request.contextPath}/applicant/cv" enctype="multipart/form-data" class="needs-validation" novalidate>
                    <input class="form-control mb-3" type="file" name="cv" accept=".pdf,.doc,.docx" required>
                    <button class="btn btn-primary" type="submit">${hasUploadedCv ? 'Replace CV' : 'Upload CV'}</button>
                    <a class="btn btn-outline-secondary ms-2" href="${pageContext.request.contextPath}/applicant/profile">Back to profile</a>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
