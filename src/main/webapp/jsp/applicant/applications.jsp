<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="My Applications"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">My applications</h1>
        <p class="text-muted mb-0">Review the jobs you have already applied for.</p>
    </div>
    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/applicant/jobs">Browse jobs</a>
</div>
<c:choose>
    <c:when test="${empty applications}">
        <div class="alert alert-secondary">You have not submitted any applications yet.</div>
    </c:when>
    <c:otherwise>
        <div class="card shadow-sm">
            <div class="table-responsive">
                <table class="table table-hover mb-0">
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
                                <div>${empty job ? application.jobId : job.title}</div>
                                <div class="small text-muted">${applicationStatusSummaries[statusName]}</div>
                            </td>
                            <td>${empty job ? '-' : job.department}</td>
                            <td>${application.appliedAt}</td>
                            <td>
                                <span class="badge app-status ${applicationStatusBadgeClasses[statusName]}">${application.status}</span>
                            </td>
                            <td>
                                <div class="d-flex gap-2 justify-content-end">
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
