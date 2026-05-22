<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Applicant Profile"/>
<c:set var="pageSection" value="applicant-profile"/>
<%@ include file="../common/header.jsp" %>
<section class="page-hero profile-hero">
    <div class="hero-copy">
        <div class="section-kicker">Profile builder</div>
        <h1 class="h2 mb-2">Applicant profile</h1>
        <p class="mb-0">Keep the essentials grouped, make your strengths easy to scan, and stay ready for the roles you want to pursue.</p>
    </div>
    <div class="hero-actions">
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/applicant/applications">My applications</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/applicant/jobs">Browse jobs</a>
    </div>
</section>

<div class="profile-shell">
    <section class="workspace-card profile-form-card">
        <div class="card-body">
            <form method="post" action="${pageContext.request.contextPath}/applicant/profile" class="needs-validation" novalidate>
                <div class="profile-section">
                    <div class="profile-section-header">
                        <div>
                            <div class="section-kicker">Core details</div>
                            <h2 class="section-title">Personal basics</h2>
                        </div>
                        <p class="text-muted mb-0">These details are used to identify you across applications and reviewer screens.</p>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Full name</label>
                            <input class="form-control" type="text" name="fullName" required value="${profile.fullName}">
                            <div class="invalid-feedback">Please enter your full name.</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone</label>
                            <input class="form-control" type="tel" name="phone" required value="${profile.phone}" pattern="[+0-9()\-\s]{6,20}">
                            <div class="invalid-feedback">Please enter a valid phone number.</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Student ID</label>
                            <input class="form-control" type="text" name="studentId" required value="${profile.studentId}">
                            <div class="invalid-feedback">Please enter your student ID.</div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Programme</label>
                            <input class="form-control" type="text" name="programme" required value="${profile.programme}">
                            <div class="invalid-feedback">Please enter your programme.</div>
                        </div>
                        <div class="col-12">
                            <label class="form-label">Bio</label>
                            <textarea class="form-control" name="bio" rows="4" placeholder="Summarise your teaching, tutoring, or academic strengths.">${profile.bio}</textarea>
                        </div>
                    </div>
                </div>

                <div class="profile-section">
                    <div class="profile-section-header">
                        <div>
                            <div class="section-kicker">Signals</div>
                            <h2 class="section-title">Skills snapshot</h2>
                        </div>
                        <p class="text-muted mb-0">These skills feed later readiness, matching, and reviewer context.</p>
                    </div>
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">Skills</label>
                            <textarea class="form-control" name="skills" rows="4"
                                      placeholder="Use commas or new lines, e.g. Java, SQL, Communication">${skillsText}</textarea>
                            <div class="form-text">Use concise entries so they are easy to match against job requirements.</div>
                        </div>
                    </div>
                </div>

                <div class="profile-section">
                    <div class="profile-section-header">
                        <div>
                            <div class="section-kicker">Availability</div>
                            <h2 class="section-title">Preferred working days</h2>
                        </div>
                        <p class="text-muted mb-0">Share when you are most available so organisers can quickly judge fit.</p>
                    </div>
                    <div class="row g-3">
                        <div class="col-12">
                            <label class="form-label">Preferred working days</label>
                            <textarea class="form-control" name="preferredWorkingDays" rows="3"
                                      placeholder="For example: Monday, Wednesday">${preferredWorkingDaysText}</textarea>
                        </div>
                    </div>
                </div>
                <button class="btn btn-primary mt-2" type="submit">Save profile</button>
            </form>
        </div>
    </section>

    <aside class="profile-aside">
        <div class="workspace-card checklist-card">
            <div class="card-body">
                <div class="checklist-art" aria-hidden="true"></div>
                <div class="section-kicker mb-2">Readiness</div>
                <h2 class="h5 mb-3">Application checklist</h2>
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
                <div class="status-prompt mt-3">
                    <c:choose>
                        <c:when test="${empty currentCvFileName}">Upload your CV to complete the full application setup.</c:when>
                        <c:otherwise>Current CV: ${currentCvFileName}</c:otherwise>
                    </c:choose>
                </div>
                <a class="btn btn-outline-primary mt-3 w-100" href="${pageContext.request.contextPath}/applicant/cv">Manage CV</a>
            </div>
        </div>
    </aside>
</div>
<%@ include file="../common/footer.jsp" %>
