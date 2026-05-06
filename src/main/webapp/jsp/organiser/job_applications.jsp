<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Job Applications"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Organiser application queue"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">Applications for ${job.title}</h1>
        <p class="text-muted mb-0">${job.department} | ${job.hoursPerWeek} hours/week | Deadline ${job.deadline}</p>
    </div>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/organiser/jobs">Back to jobs</a>
</div>
<div class="alert ${jobFull ? 'alert-warning' : 'alert-info'}">
    Recruitment progress: <strong>${acceptedCount} / ${job.assistantQuota}</strong> accepted.
    Remaining slots: <strong>${remainingAssistantSlots}</strong>.
    <c:if test="${jobFull}">The job is full and pending/reviewing applications should already be rejected.</c:if>
</div>
<c:choose>
    <c:when test="${empty applications}">
        <div class="alert alert-secondary">No applicants have submitted an application for this job yet.</div>
    </c:when>
    <c:otherwise>
        <div class="card shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
                    <thead>
                    <tr>
                        <th>Applicant</th>
                        <th>Student ID</th>
                        <th>Programme</th>
                        <th>Match Score</th>
                        <th>Applied at</th>
                        <th>Status</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${applications}" var="application">
                        <c:set var="applicant" value="${applicantsByUserId[application.applicantUserId]}"/>
                        <c:set var="skillMatch" value="${skillMatchesByApplicationId[application.id]}"/>
                        <tr>
                            <td>${empty applicant ? application.applicantUserId : applicant.fullName}</td>
                            <td>${empty applicant ? '-' : applicant.studentId}</td>
                            <td>${empty applicant ? '-' : applicant.programme}</td>
                            <td>
                                <span class="badge ${skillMatch.matchScore >= 75 ? 'text-bg-success' : skillMatch.matchScore >= 40 ? 'text-bg-warning' : 'text-bg-secondary'}">
                                    ${skillMatch.matchScore}% match
                                </span>
                            </td>
                            <td>${application.appliedAt}</td>
                            <td>
                                <span class="badge app-status ${applicationStatusBadgeClasses[application.status]}">${application.status}</span>
                            </td>
                            <td class="text-end">
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${pageContext.request.contextPath}/organiser/applications/detail?id=${application.id}">
                                    View
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../common/footer.jsp" %>
