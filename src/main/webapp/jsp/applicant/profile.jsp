<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Applicant Profile"/>
<%@ include file="../common/header.jsp" %>
<div class="row g-4">
    <div class="col-lg-7">
        <div class="card shadow-sm">
            <div class="card-body">
                <h1 class="h3 mb-3">Applicant profile</h1>
                <form method="post" action="${pageContext.request.contextPath}/applicant/profile">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Full name</label>
                            <input class="form-control" type="text" name="fullName" required value="${profile.fullName}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone</label>
                            <input class="form-control" type="text" name="phone" required value="${profile.phone}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Student ID</label>
                            <input class="form-control" type="text" name="studentId" required value="${profile.studentId}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Programme</label>
                            <input class="form-control" type="text" name="programme" required value="${profile.programme}">
                        </div>
                        <div class="col-12">
                            <label class="form-label">Bio</label>
                            <textarea class="form-control" name="bio" rows="4">${profile.bio}</textarea>
                        </div>
                    </div>
                    <button class="btn btn-primary mt-3" type="submit">Save profile</button>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card shadow-sm">
            <div class="card-body">
                <h2 class="h5 mb-3">Upload CV</h2>
                <p class="small text-muted">Accepted formats: PDF, DOC, DOCX.</p>
                <form method="post" action="${pageContext.request.contextPath}/applicant/cv" enctype="multipart/form-data">
                    <input class="form-control mb-3" type="file" name="cv" accept=".pdf,.doc,.docx" required>
                    <button class="btn btn-outline-primary" type="submit">Upload CV</button>
                </form>
                <c:if test="${not empty profile.cvFileName}">
                    <p class="mt-3 mb-0"><strong>Current file:</strong> ${profile.cvFileName}</p>
                </c:if>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
