<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Available Jobs"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h1 class="h3 mb-1">Available TA jobs</h1>
        <p class="text-muted mb-0">Browse current openings published by organisers.</p>
    </div>
    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/applicant/profile">My Profile</a>
</div>
<c:if test="${empty profile}">
    <div class="alert alert-warning">Complete your profile before uploading a CV or preparing to apply in Iteration 2.</div>
</c:if>
<div class="row g-4">
    <c:forEach items="${jobs}" var="job">
        <div class="col-md-6 col-xl-4">
            <div class="card h-100 shadow-sm">
                <div class="card-body">
                    <h2 class="h5">${job.title}</h2>
                    <p class="text-muted mb-2">${job.department}</p>
                    <p class="small">${job.description}</p>
                    <p class="mb-2"><strong>Hours/week:</strong> ${job.hoursPerWeek}</p>
                    <p class="mb-3"><strong>Deadline:</strong> ${job.deadline}</p>
                    <div class="mb-3">
                        <c:forEach items="${job.requirements}" var="requirement">
                            <span class="badge text-bg-light me-1 mb-1">${requirement}</span>
                        </c:forEach>
                    </div>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/applicant/job-detail?id=${job.id}">View details</a>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<c:if test="${empty jobs}">
    <div class="alert alert-secondary">No open jobs are available yet.</div>
</c:if>
<%@ include file="../common/footer.jsp" %>
