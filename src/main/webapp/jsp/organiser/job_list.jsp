<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Organiser Jobs"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4">
    <div>
        <h1 class="h3 mb-1">My job postings</h1>
        <p class="text-muted mb-0">Create and review published TA positions.</p>
    </div>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/organiser/jobs/create">Create job</a>
</div>
<div class="card shadow-sm">
    <div class="card-body">
        <div class="table-responsive">
            <table class="table align-middle">
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Department</th>
                    <th>Hours/week</th>
                    <th>Deadline</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${jobs}" var="job">
                    <tr>
                        <td>${job.title}</td>
                        <td>${job.department}</td>
                        <td>${job.hoursPerWeek}</td>
                        <td>${job.deadline}</td>
                        <td><span class="badge text-bg-success">${job.status}</span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
        <c:if test="${empty jobs}">
            <div class="alert alert-secondary mt-3 mb-0">You have not created any jobs yet.</div>
        </c:if>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
