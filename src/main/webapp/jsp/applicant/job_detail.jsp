<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Job Detail"/>
<%@ include file="../common/header.jsp" %>
<c:choose>
    <c:when test="${empty job}">
        <div class="alert alert-danger">Job not found.</div>
    </c:when>
    <c:otherwise>
        <a class="btn btn-link px-0 mb-3" href="${pageContext.request.contextPath}/applicant/jobs">&larr; Back to jobs</a>
        <div class="row g-4">
            <div class="col-lg-8">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h1 class="h3">${job.title}</h1>
                        <p class="text-muted">${job.department}</p>
                        <p>${job.description}</p>
                        <hr>
                        <p><strong>Hours/week:</strong> ${job.hoursPerWeek}</p>
                        <p><strong>Deadline:</strong> ${job.deadline}</p>
                        <h2 class="h5 mt-4">Requirements</h2>
                        <ul>
                            <c:forEach items="${job.requirements}" var="requirement">
                                <li>${requirement}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h5">Application readiness</h2>
                        <ul class="list-unstyled small">
                            <li class="mb-2">Profile: <strong>${empty profile ? 'Missing' : 'Ready'}</strong></li>
                            <li class="mb-2">CV: <strong>${hasUploadedCv ? 'Uploaded' : 'Missing'}</strong></li>
                            <li class="mb-3">Status:
                                <strong>
                                    <c:choose>
                                        <c:when test="${empty existingApplication}">Not applied</c:when>
                                        <c:otherwise>
                                            <span class="badge app-status ${applicationStatusBadgeClasses[existingApplication.status]}">
                                                ${existingApplication.status}
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </strong>
                            </li>
                        </ul>
                        <c:choose>
                            <c:when test="${not empty existingApplication}">
                                <div class="alert alert-light border small">
                                    <div class="fw-semibold mb-1">You already applied for this job.</div>
                                    <div>${applicationStatusSummaries[existingApplication.status]}</div>
                                </div>
                                <a class="btn btn-outline-primary w-100"
                                   href="${pageContext.request.contextPath}/applicant/applications/detail?id=${existingApplication.id}">
                                    View application
                                </a>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/applicant/job-detail" class="d-grid gap-2">
                                    <input type="hidden" name="id" value="${job.id}">
                                    <button class="btn btn-primary" type="submit">Apply now</button>
                                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/profile">Update profile</a>
                                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/cv">Manage CV</a>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../common/footer.jsp" %>
