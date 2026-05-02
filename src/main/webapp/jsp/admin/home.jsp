<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Admin Home"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Admin dashboard metrics"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">Admin Dashboard</h1>
        <p class="text-muted mb-0">Monitor platform activity, applicant workload, and quota-related risks.</p>
    </div>
    <div class="d-flex flex-wrap gap-2">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/settings">Settings</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/users">Users</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/workloads">Workloads</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/jobs">Jobs</a>
    </div>
</div>
<div class="row g-3 mb-4">
    <div class="col-md-6 col-xl-3">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="small text-muted">Users</div>
                <div class="h3 mb-1">${dashboardSummary.totalUsers}</div>
                <div class="small text-muted">
                    ${dashboardSummary.totalApplicants} applicants / ${dashboardSummary.totalOrganisers} organisers
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xl-3">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="small text-muted">Jobs</div>
                <div class="h3 mb-1">${dashboardSummary.totalJobs}</div>
                <div class="small text-muted">
                    ${dashboardSummary.openJobs} open / ${dashboardSummary.fullJobs} full
                </div>
            </div>
        </div>
    </div>
</div>
<div class="row g-3 mb-4">
    <div class="col-md-6 col-xl-3">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="small text-muted">Applications</div>
                <div class="h3 mb-1">${dashboardSummary.totalApplications}</div>
                <div class="small text-muted">
                    ${dashboardSummary.pendingOrReviewingApplications} pending or reviewing
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xl-3">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="small text-muted">Accepted Applications</div>
                <div class="h3 mb-1">${dashboardSummary.acceptedApplications}</div>
                <div class="small text-muted">Accepted across all jobs</div>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xl-3">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="small text-muted">Applicant Limit Risks</div>
                <div class="h3 mb-1">${dashboardSummary.applicantsAtLimit}</div>
                <div class="small text-muted">
                    ${applicantsUsingOverrideCount} using override / ${applicantsOverLimitCount} over limit
                </div>
            </div>
        </div>
    </div>
    <div class="col-md-6 col-xl-3">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="small text-muted">Workload Threshold</div>
                <div class="h3 mb-1">${dashboardSummary.overloadedApplicants}</div>
                <div class="small text-muted">
                    Over ${defaultWorkloadThreshold} hours/week, default limit ${defaultApplicantApplicationLimit}
                </div>
            </div>
        </div>
    </div>
</div>
<div class="row g-4">
    <div class="col-lg-6">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h2 class="h5 mb-0">Overloaded Applicants</h2>
                    <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/workloads">View Workloads</a>
                </div>
                <c:choose>
                    <c:when test="${empty overloadedApplicantViews}">
                        <div class="alert alert-success mb-0">No applicant is above the default workload threshold.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table align-middle mb-0">
                                <thead>
                                <tr>
                                    <th>Applicant</th>
                                    <th>Hours/Week</th>
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
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card shadow-sm h-100">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h2 class="h5 mb-0">Quota Risk Jobs</h2>
                    <a class="btn btn-sm btn-outline-primary" href="${pageContext.request.contextPath}/admin/jobs">View Jobs</a>
                </div>
                <c:choose>
                    <c:when test="${empty jobRiskViews}">
                        <div class="alert alert-success mb-0">No full jobs are carrying unexpected pending or reviewing applications.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="table-responsive">
                            <table class="table align-middle mb-0">
                                <thead>
                                <tr>
                                    <th>Job</th>
                                    <th>Accepted / Quota</th>
                                    <th>Pending + Reviewing</th>
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
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
