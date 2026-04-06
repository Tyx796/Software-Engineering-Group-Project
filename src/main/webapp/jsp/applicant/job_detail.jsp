<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Job Detail"/>
<%@ include file="../common/header.jsp" %>
<c:choose>
    <c:when test="${empty job}">
        <div class="alert alert-danger">Job not found.</div>
    </c:when>
    <c:otherwise>
        <a class="btn btn-link px-0 mb-3" href="${pageContext.request.contextPath}/applicant/jobs">&larr; Back to jobs</a>
        <div class="row g-4">
            <div class="col-lg-8">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h1 class="h3">${job.title}</h1>
                        <p class="text-muted">${job.department}</p>
                        <p>${job.description}</p>
                        <hr>
                        <p><strong>Hours/week:</strong> ${job.hoursPerWeek}</p>
                        <p><strong>Deadline:</strong> ${job.deadline}</p>
                        <h2 class="h5 mt-4">Requirements</h2>
                        <ul>
                            <c:forEach items="${job.requirements}" var="requirement">
                                <li>${requirement}</li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="card shadow-sm">
                    <div class="card-body">
                        <h2 class="h5">Application readiness</h2>
                        <ul class="list-unstyled small">
                            <li class="mb-2">Profile: <strong>${empty profile ? 'Missing' : 'Ready'}</strong></li>
                            <li class="mb-2">CV: <strong>${empty profile.cvFileName ? 'Missing' : 'Uploaded'}</strong></li>
                            <li>Apply action will be added in Iteration 2.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../common/footer.jsp" %>
