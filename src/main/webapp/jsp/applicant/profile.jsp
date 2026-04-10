<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Applicant Profile"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">Applicant profile</h1>
        <p class="text-muted mb-0">Complete your profile to get ready for TA applications.</p>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/applicant/applications">My applications</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/applicant/jobs">Browse jobs</a>
    </div>
</div>
<div class="row g-4">
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/applicant/profile" class="needs-validation" novalidate>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Full name</label>
                            <input class="form-control" type="text" name="fullName" required value="${profile.fullName}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone</label>
                            <input class="form-control" type="tel" name="phone" required value="${profile.phone}" pattern="[+0-9()\-\s]{6,20}">
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
                            <textarea class="form-control" name="bio" rows="4" placeholder="Summarise your teaching, tutoring, or academic strengths.">${profile.bio}</textarea>
                        </div>
                        <div class="col-12">
                            <label class="form-label">Skills</label>
                            <textarea class="form-control" name="skills" rows="3"
                                      placeholder="Use commas or new lines, e.g. Java, SQL, Communication">${skillsText}</textarea>
                            <div class="form-text">These skills will be used later for application readiness and review.</div>
                        </div>
                        <div class="col-12">
                            <label class="form-label">Preferred working days</label>
                            <textarea class="form-control" name="preferredWorkingDays" rows="2"
                                      placeholder="For example: Monday, Wednesday">${preferredWorkingDaysText}</textarea>
                        </div>
                    </div>
                    <button class="btn btn-primary mt-3" type="submit">Save profile</button>
                </form>
            </div>
        </div>
    </div>
    <div class="col-lg-4">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <h2 class="h5 mb-3">Iteration 1 checklist</h2>
                <ul class="list-group list-group-flush small readiness-list">
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>Profile details</span>
                        <strong>${empty profile.fullName ? 'Pending' : 'Done'}</strong>
                    </li>
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>Skills added</span>
                        <strong>${empty profile.skills ? 'Pending' : 'Done'}</strong>
                    </li>
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>Availability added</span>
                        <strong>${empty profile.preferredWorkingDays ? 'Pending' : 'Done'}</strong>
                    </li>
                    <li class="list-group-item d-flex justify-content-between px-0">
                        <span>CV uploaded</span>
                        <c:choose>
                            <c:when test="${not hasUploadedCv}"><strong>Pending</strong></c:when>
                            <c:otherwise><a href="${pageContext.request.contextPath}/applicant/cv/download"><strong>View</strong></a></c:otherwise>
                        </c:choose>
                    </li>
                </ul>
                <a class="btn btn-outline-primary mt-3 w-100" href="${pageContext.request.contextPath}/applicant/cv">Manage CV</a>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
