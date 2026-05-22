<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Job Supervision"/>
<c:set var="pageSection" value="admin-jobs"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Admin job supervision"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">Jobs and Applications Supervision</h1>
        <p class="text-muted mb-0">Monitor quota usage and global application status distribution across all jobs.</p>
    </div>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/home">Back to dashboard</a>
</div>
<div class="card shadow-sm">
    <div class="card-body">
        <c:choose>
            <c:when test="${empty jobSupervisionViews}">
                <div class="alert alert-secondary mb-0">No jobs are available.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                        <tr>
                            <th>Job</th>
                            <th>Organiser</th>
                            <th>Accepted / Quota</th>
                            <th>Remaining</th>
                            <th>Pending</th>
                            <th>Reviewing</th>
                            <th>Rejected</th>
                            <th>Withdrawn</th>
                            <th>Cancelled</th>
                            <th>Flags</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${jobSupervisionViews}" var="view">
                            <tr class="${view.acceptedOverQuota || view.unexpectedPendingOrReviewingWhenFull ? 'table-warning' : ''}">
                                <td>
                                    <div class="fw-semibold">${view.job.title}</div>
                                    <div class="small text-muted">${view.job.department}</div>
                                    <div class="small text-muted">Status ${view.job.status}</div>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty view.organiser}">
                                            <span class="text-muted">Unknown organiser</span>
                                        </c:when>
                                        <c:otherwise>
                                            <div>${view.organiser.username}</div>
                                            <div class="small text-muted">${view.organiser.email}</div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <strong>${view.acceptedCount} / ${view.job.assistantQuota}</strong>
                                    <c:if test="${view.full}">
                                        <div class="small text-danger">Full</div>
                                    </c:if>
                                </td>
                                <td>${view.remainingSlots}</td>
                                <td>${view.pendingCount}</td>
                                <td>${view.reviewingCount}</td>
                                <td>${view.rejectedCount}</td>
                                <td>${view.withdrawnCount}</td>
                                <td>${view.cancelledCount}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${view.acceptedOverQuota}">
                                            <div class="small text-danger">Accepted count exceeds quota</div>
                                        </c:when>
                                        <c:when test="${view.unexpectedPendingOrReviewingWhenFull}">
                                            <div class="small text-warning">Full job still has pending or reviewing applications</div>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">Normal</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
