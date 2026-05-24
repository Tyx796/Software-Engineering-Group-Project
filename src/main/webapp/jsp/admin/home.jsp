<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Admin Home"/>
<c:set var="pageSection" value="admin-home"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Admin dashboard metrics"/>
<%@ include file="../common/header.jsp" %>
<section class="page-hero">
    <div>
        <div class="section-kicker">Admin Workspace</div>
        <h1 class="h2 mb-2">Admin Dashboard</h1>
        <p class="text-muted mb-0">Monitor platform activity, identify workload risks, and keep application quotas under control.</p>
    </div>
    <div class="action-cluster">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/settings">Settings</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/users">Users</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/workloads">Workloads</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/jobs">Jobs</a>
    </div>
</section>
<section class="dashboard-grid">
    <div class="metric-card">
        <div class="metric-label">Users</div>
        <div class="metric-value">${dashboardSummary.totalUsers}</div>
        <div class="metric-meta">${dashboardSummary.totalApplicants} applicants / ${dashboardSummary.totalOrganisers} organisers</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Jobs</div>
        <div class="metric-value">${dashboardSummary.totalJobs}</div>
        <div class="metric-meta">${dashboardSummary.openJobs} open / ${dashboardSummary.fullJobs} full</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Applications</div>
        <div class="metric-value">${dashboardSummary.totalApplications}</div>
        <div class="metric-meta">${dashboardSummary.pendingOrReviewingApplications} pending or reviewing</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Accepted applications</div>
        <div class="metric-value">${dashboardSummary.acceptedApplications}</div>
        <div class="metric-meta">Accepted across all jobs.</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Applicant limit risks</div>
        <div class="metric-value">${dashboardSummary.applicantsAtLimit}</div>
        <div class="metric-meta">${applicantsUsingOverrideCount} using override / ${applicantsOverLimitCount} over limit</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Workload threshold</div>
        <div class="metric-value">${dashboardSummary.overloadedApplicants}</div>
        <div class="metric-meta">Over ${defaultWorkloadThreshold} hours/week, default limit ${defaultApplicantApplicationLimit}</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">At-risk jobs</div>
        <div class="metric-value">${fn:length(jobRiskViews)}</div>
        <div class="metric-meta">Jobs currently carrying quota or pipeline risk signals.</div>
    </div>
    <div class="metric-card">
        <div class="metric-label">Default limit</div>
        <div class="metric-value">${defaultApplicantApplicationLimit}</div>
        <div class="metric-meta">Baseline active application cap for new applicants.</div>
    </div>
</section>
<section class="dashboard-grid">
    <div class="workspace-card insight-panel">
        <div class="insight-panel-header">
            <div>
                <h2 class="section-title mb-1">Overloaded applicants</h2>
                <p class="text-muted mb-0">Candidates who have moved past the default weekly workload threshold.</p>
            </div>
            <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/workloads">View workloads</a>
        </div>
        <c:choose>
            <c:when test="${empty overloadedApplicantViews}">
                <div class="alert alert-success mb-0">No applicant is above the default workload threshold.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table workspace-table align-middle mb-0">
                        <thead>
                        <tr>
                            <th>Applicant</th>
                            <th>Hours / week</th>
                            <th>Assignments</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${overloadedApplicantViews}" var="view">
                            <tr>
                                <td>
                                    <div class="fw-semibold">${view.user.username}</div>
                                    <div class="small text-muted">${view.user.email}</div>
                                </td>
                                <td>
                                    <div class="text-danger fw-semibold">${view.totalHoursPerWeek}</div>
                                    <div class="small text-muted">${view.workloadAlertMessage}</div>
                                </td>
                                <td>${fn:length(view.acceptedAssignments)}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="workspace-card insight-panel">
        <div class="insight-panel-header">
            <div>
                <h2 class="section-title mb-1">Quota risk jobs</h2>
                <p class="text-muted mb-0">Full jobs that still carry unexpected pending or reviewing applications.</p>
            </div>
            <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/jobs">View jobs</a>
        </div>
        <c:choose>
            <c:when test="${empty jobRiskViews}">
                <div class="alert alert-success mb-0">No full jobs are carrying unexpected pending or reviewing applications.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table workspace-table align-middle mb-0">
                        <thead>
                        <tr>
                            <th>Job</th>
                            <th>Accepted / quota</th>
                            <th>Pending + reviewing</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${jobRiskViews}" var="view">
                            <tr class="${view.acceptedOverQuota || view.unexpectedPendingOrReviewingWhenFull ? 'table-warning' : ''}">
                                <td>
                                    <div class="fw-semibold">${view.job.title}</div>
                                    <div class="small text-muted">${view.job.department}</div>
                                </td>
                                <td>${view.acceptedCount} / ${view.job.assistantQuota}</td>
                                <td>${view.pendingCount + view.reviewingCount}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>
<%@ include file="../common/footer.jsp" %>
