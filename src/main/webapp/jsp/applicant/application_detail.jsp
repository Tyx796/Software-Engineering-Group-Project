<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Application Detail"/>
<%@ include file="../common/header.jsp" %>
<c:choose>
    <c:when test="${empty application}">
        <div class="alert alert-danger">Application not found.</div>
    </c:when>
    <c:otherwise>
        <a class="btn btn-link px-0 mb-3" href="${pageContext.request.contextPath}/applicant/applications">&larr; Back to applications</a>
        <div class="row g-4">
            <div class="col-lg-8">
                <c:set var="statusName" value="${application.status}"/>
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h1 class="h3">${empty job ? 'Application' : job.title}</h1>
                        <p class="text-muted">${empty job ? application.jobId : job.department}</p>
                        <div class="alert alert-light border mb-3">
                            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                                <div>
                                    <strong>Status:</strong>
                                    <span class="badge app-status ${applicationStatusBadgeClasses[statusName]}">${application.status}</span>
                                </div>
                                <span class="small text-muted">${applicationStatusSummaries[statusName]}</span>
                            </div>
                        </div>
                        <p><strong>Applied at:</strong> ${application.appliedAt}</p>
                        <c:if test="${not empty application.reviewedAt}">
                            <p><strong>First reviewed at:</strong> ${application.reviewedAt}</p>
                        </c:if>
                        <c:if test="${statusName == 'PENDING' || statusName == 'REVIEWING' || statusName == 'ACCEPTED'}">
                            <form method="post" action="${pageContext.request.contextPath}/applicant/applications/withdraw" class="mt-3">
                                <input type="hidden" name="applicationId" value="${application.id}">
                                <input type="hidden" name="returnTo" value="detail">
                                <button class="btn btn-outline-danger" type="submit">Withdraw application</button>
                            </form>
                        </c:if>
                        <c:if test="${not empty job}">
                            <hr>
                            <p>${job.description}</p>
                            <p><strong>Hours/week:</strong> ${job.hoursPerWeek}</p>
                            <p><strong>Deadline:</strong> ${job.deadline}</p>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../common/footer.jsp" %>
