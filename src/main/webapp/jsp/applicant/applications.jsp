<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My Applications"/>
<c:set var="pageSection" value="applicant-applications"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Applicant application status"/>
<%@ include file="../common/header.jsp" %>
<section class="page-hero applications-hero">
    <div class="hero-copy">
        <div class="section-kicker">Application tracker</div>
        <h1 class="h2 mb-2">My applications</h1>
        <p class="mb-0">Keep the status clear, spot your next action quickly, and monitor how much application capacity you still have.</p>
    </div>
    <div class="hero-actions">
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/applicant/jobs">Browse jobs</a>
    </div>
</section>

<section class="workspace-card applications-summary-card">
    <div class="card-body applications-summary">
        <div>
            <span class="strip-label">Active application usage</span>
            <strong>${activeApplicationCount} / ${effectiveApplicationLimit}</strong>
            <p class="text-muted mb-0">Contact Admin if you need an adjustment to your active application limit.</p>
        </div>
        <div class="capacity-progress-track" aria-hidden="true">
            <div class="capacity-progress-fill"
                 style="width: ${(activeApplicationCount * 100) / (effectiveApplicationLimit == 0 ? 1 : effectiveApplicationLimit)}%;"></div>
        </div>
        <div class="status-prompt">Review detail pages for role-specific updates and withdrawal options.</div>
    </div>
</section>

<c:choose>
    <c:when test="${empty applications}">
        <div class="editorial-empty-state">
            <div class="editorial-empty-art"></div>
            <div class="editorial-empty-copy">
                <div class="section-kicker">No applications yet</div>
                <h2 class="section-title">You have not submitted any applications yet.</h2>
                <p class="text-muted mb-3">Start with the roles that best match your current skills and availability.</p>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/applicant/jobs">Browse open jobs</a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="workspace-card application-collection">
            <div class="table-responsive">
                <table class="table table-hover workspace-table mb-0">
                    <thead>
                    <tr>
                        <th>Job</th>
                        <th>Department</th>
                        <th>Applied at</th>
                        <th>Status</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${applications}" var="application">
                        <c:set var="job" value="${jobsById[application.jobId]}"/>
                        <c:set var="statusName" value="${application.status}"/>
                        <tr>
                            <td>
                                <div class="fw-semibold">${empty job ? application.jobId : job.title}</div>
                                <c:if test="${not empty job}">
                                    <div class="small text-muted application-row-meta">
                                        Slots ${acceptedCountsByJobId[job.id]} / ${job.assistantQuota}
                                        <span class="badge ${fullJobsById[job.id] ? 'text-bg-warning' : 'text-bg-success'} ms-1">
                                            ${fullJobsById[job.id] ? 'Full' : 'Open'}
                                        </span>
                                    </div>
                                </c:if>
                                <div class="small text-muted">${applicationStatusSummaries[statusName]}</div>
                            </td>
                            <td>${empty job ? '-' : job.department}</td>
                            <td>${application.appliedAt}</td>
                            <td>
                                <span class="badge app-status ${applicationStatusBadgeClasses[statusName]}">${application.status}</span>
                            </td>
                            <td>
                                <div class="d-flex gap-2 justify-content-end application-actions">
                                    <a class="btn btn-sm btn-outline-primary"
                                       href="${pageContext.request.contextPath}/applicant/applications/detail?id=${application.id}">
                                        View
                                    </a>
                                    <c:if test="${statusName == 'PENDING' || statusName == 'REVIEWING' || statusName == 'ACCEPTED'}">
                                        <form method="post" action="${pageContext.request.contextPath}/applicant/applications/withdraw" class="m-0">
                                            <input type="hidden" name="applicationId" value="${application.id}">
                                            <button class="btn btn-sm btn-outline-danger" type="submit">Withdraw</button>
                                        </form>
                                    </c:if>
                                </div>
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
