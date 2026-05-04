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
<div class="card shadow-sm mb-4">
    <div class="card-body">
        <form method="get" action="${pageContext.request.contextPath}/organiser/jobs/applications" class="row g-3 align-items-end">
            <input type="hidden" name="jobId" value="${job.id}">
            <div class="col-md-4">
                <label class="form-label" for="keyword">Search applicant</label>
                <input class="form-control" id="keyword" type="text" name="keyword" value="${keyword}"
                       placeholder="Name, student ID, programme, or user ID">
            </div>
            <div class="col-md-3">
                <label class="form-label" for="status">Status</label>
                <select class="form-select" id="status" name="status">
                    <option value="" ${empty statusFilter ? 'selected' : ''}>All statuses</option>
                    <option value="PENDING" ${statusFilter == 'PENDING' ? 'selected' : ''}>Pending</option>
                    <option value="REVIEWING" ${statusFilter == 'REVIEWING' ? 'selected' : ''}>Reviewing</option>
                    <option value="ACCEPTED" ${statusFilter == 'ACCEPTED' ? 'selected' : ''}>Accepted</option>
                    <option value="REJECTED" ${statusFilter == 'REJECTED' ? 'selected' : ''}>Rejected</option>
                    <option value="WITHDRAWN" ${statusFilter == 'WITHDRAWN' ? 'selected' : ''}>Withdrawn</option>
                    <option value="CANCELLED" ${statusFilter == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                </select>
            </div>
            <div class="col-md-3">
                <label class="form-label" for="sort">Sort by</label>
                <select class="form-select" id="sort" name="sort">
                    <option value="appliedAt" ${sortOption == 'appliedAt' ? 'selected' : ''}>Applied time</option>
                    <option value="match" ${sortOption == 'match' ? 'selected' : ''}>Match score</option>
                    <option value="status" ${sortOption == 'status' ? 'selected' : ''}>Status</option>
                </select>
            </div>
            <div class="col-md-2 d-flex gap-2">
                <button class="btn btn-primary flex-grow-1" type="submit">Apply</button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/organiser/jobs/applications?jobId=${job.id}">Reset</a>
            </div>
        </form>
    </div>
</div>
<c:choose>
    <c:when test="${empty reviewViews}">
        <div class="alert alert-secondary">
                ${hasActiveFilters ? 'No applications match the current filters.' : 'No applicants have submitted an application for this job yet.'}
        </div>
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
                    <c:forEach items="${reviewViews}" var="view">
                        <tr>
                            <td>${empty view.applicant ? view.application.applicantUserId : view.applicant.fullName}</td>
                            <td>${empty view.applicant ? '-' : view.applicant.studentId}</td>
                            <td>${empty view.applicant ? '-' : view.applicant.programme}</td>
                            <td>
                                <span class="badge ${view.skillMatch.matchScore >= 75 ? 'text-bg-success' : view.skillMatch.matchScore >= 40 ? 'text-bg-warning' : 'text-bg-secondary'}">
                                    ${view.skillMatch.matchScore}% match
                                </span>
                            </td>
                            <td>${view.application.appliedAt}</td>
                            <td>
                                <span class="badge app-status ${applicationStatusBadgeClasses[view.application.status]}">${view.application.status}</span>
                            </td>
                            <td class="text-end">
                                <a class="btn btn-sm btn-outline-primary"
                                   href="${pageContext.request.contextPath}/organiser/applications/detail?id=${view.application.id}">
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
