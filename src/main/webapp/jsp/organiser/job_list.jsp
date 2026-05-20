<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Organiser Jobs"/>
<c:set var="pageSection" value="organiser-jobs"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Organiser job recruitment progress"/>
<%@ include file="../common/header.jsp" %>
<section class="page-hero">
    <div>
        <div class="section-kicker">Organiser Workspace</div>
        <h1 class="h2 mb-2">My job postings</h1>
        <p class="text-muted mb-0">Review recruitment progress, keep track of quota usage, and move quickly into candidate review.</p>
    </div>
    <div class="page-actions">
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/organiser/jobs/create">Create job</a>
    </div>
</section>
<section class="workspace-card">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table workspace-table align-middle">
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Department</th>
                    <th>Slots</th>
                    <th>Hours/week</th>
                    <th>Deadline</th>
                    <th>Status</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${jobs}" var="job">
                    <tr>
                        <td>
                            <div class="fw-semibold">${job.title}</div>
                            <div class="small text-muted">${job.department}</div>
                        </td>
                        <td>${job.department}</td>
                        <td>
                            <div class="fw-semibold">${acceptedCountsByJobId[job.id]} / ${job.assistantQuota}</div>
                            <div class="small text-muted">Remaining ${remainingSlotsByJobId[job.id]}</div>
                        </td>
                        <td>${job.hoursPerWeek}</td>
                        <td>${job.deadline}</td>
                        <td>
                            <span class="badge ${job.status == 'CANCELLED' ? 'text-bg-secondary' : (fullJobsById[job.id] ? 'text-bg-warning' : 'text-bg-success')}">
                                ${job.status}
                            </span>
                            <c:if test="${job.status != 'CANCELLED'}">
                                <span class="badge ${fullJobsById[job.id] ? 'text-bg-warning' : 'text-bg-success'} ms-1">
                                    ${fullJobsById[job.id] ? 'Full' : 'Open'}
                                </span>
                            </c:if>
                        </td>
                        <td class="text-end">
                            <c:if test="${job.status != 'CANCELLED'}">
                                <a class="btn btn-sm btn-outline-secondary me-2"
                                   href="${pageContext.request.contextPath}/organiser/jobs/edit?id=${job.id}">
                                    Edit
                                </a>
                                <form method="post" action="${pageContext.request.contextPath}/organiser/jobs/cancel"
                                      class="d-inline me-2">
                                    <input type="hidden" name="jobId" value="${job.id}">
                                    <button class="btn btn-sm btn-outline-danger" type="submit">Cancel Job</button>
                                </form>
                            </c:if>
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/organiser/jobs/applications?jobId=${job.id}">
                                View Applications
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <c:if test="${empty jobs}">
            <div class="empty-state mt-3 mb-0">You have not created any jobs yet.</div>
        </c:if>
    </div>
</section>
<%@ include file="../common/footer.jsp" %>
