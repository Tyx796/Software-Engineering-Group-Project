<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Application Review"/>
<%@ include file="../common/header.jsp" %>
<c:choose>
    <c:when test="${empty application}">
        <div class="alert alert-danger">Application not found.</div>
    </c:when>
    <c:otherwise>
        <a class="btn btn-link px-0 mb-3"
           href="${pageContext.request.contextPath}/organiser/jobs/applications?jobId=${application.jobId}">
            &larr; Back to applications
        </a>
        <div class="row g-4">
            <div class="col-lg-7">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h1 class="h3 mb-3">${empty applicant ? application.applicantUserId : applicant.fullName}</h1>
                        <p class="text-muted mb-4">
                            Reviewing application for ${empty job ? application.jobId : job.title}
                        </p>

                        <div class="row g-3">
                            <div class="col-md-6">
                                <div class="small text-muted">Full name</div>
                                <div>${empty applicant ? '-' : applicant.fullName}</div>
                            </div>
                            <div class="col-md-6">
                                <div class="small text-muted">Phone</div>
                                <div>${empty applicant ? '-' : applicant.phone}</div>
                            </div>
                            <div class="col-md-6">
                                <div class="small text-muted">Student ID</div>
                                <div>${empty applicant ? '-' : applicant.studentId}</div>
                            </div>
                            <div class="col-md-6">
                                <div class="small text-muted">Programme</div>
                                <div>${empty applicant ? '-' : applicant.programme}</div>
                            </div>
                        </div>

                        <c:if test="${not empty applicant and not empty applicant.bio}">
                            <hr>
                            <div class="small text-muted mb-1">Bio</div>
                            <p class="mb-0">${applicant.bio}</p>
                        </c:if>
                    </div>
                </div>
            </div>

            <div class="col-lg-5">
                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <h2 class="h5 mb-3">Application Summary</h2>
                        <p class="mb-2"><strong>Status:</strong> <span class="badge app-status">${application.status}</span></p>
                        <p class="mb-2"><strong>Applied at:</strong> ${application.appliedAt}</p>
                        <c:if test="${not empty application.reviewedAt}">
                            <p class="mb-2"><strong>First reviewed at:</strong> ${application.reviewedAt}</p>
                        </c:if>
                        <p class="mb-2"><strong>Job:</strong> ${empty job ? application.jobId : job.title}</p>
                        <c:if test="${not empty job}">
                            <p class="mb-2"><strong>Department:</strong> ${job.department}</p>
                            <p class="mb-2"><strong>Hours/week:</strong> ${job.hoursPerWeek}</p>
                            <p class="mb-2"><strong>Deadline:</strong> ${job.deadline}</p>
                            <p class="mb-0"><strong>Requirements:</strong> ${job.requirements}</p>
                        </c:if>
                    </div>
                </div>

                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <h2 class="h5 mb-3">Update Status</h2>
                        <c:choose>
                            <c:when test="${application.status == 'ACCEPTED' || application.status == 'REJECTED'}">
                                <p class="mb-0">
                                    <strong>Decision:</strong>
                                    <span class="badge app-status">${application.status}</span>
                                </p>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/organiser/applications/status">
                                    <input type="hidden" name="applicationId" value="${application.id}">
                                    <div class="d-flex flex-wrap gap-2">
                                        <button class="btn btn-success" type="submit" name="status" value="ACCEPTED">
                                            Accept
                                        </button>
                                        <button class="btn btn-danger" type="submit" name="status" value="REJECTED">
                                            Reject
                                        </button>
                                    </div>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <h2 class="h5 mb-3">Skills</h2>
                        <c:choose>
                            <c:when test="${not empty applicant and not empty applicant.skills}">
                                <ul class="mb-0">
                                    <c:forEach items="${applicant.skills}" var="skill">
                                        <li>${skill}</li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-0">No skills information provided.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <h2 class="h5 mb-3">Availability</h2>
                        <c:choose>
                            <c:when test="${not empty applicant and not empty applicant.preferredWorkingDays}">
                                <ul class="mb-0">
                                    <c:forEach items="${applicant.preferredWorkingDays}" var="day">
                                        <li>${day}</li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-0">No availability information provided.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h5 mb-3">CV</h2>
                        <c:choose>
                            <c:when test="${hasUploadedCv}">
                                <p class="mb-3"><strong>${currentCvFileName}</strong></p>
                                <a class="btn btn-outline-primary"
                                   href="${pageContext.request.contextPath}/organiser/applications/cv?applicationId=${application.id}">
                                    View / Download CV
                                </a>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-0">No CV is currently available for this applicant.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../common/footer.jsp" %>
