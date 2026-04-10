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
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${jobs}" var="job">
                    <tr>
                        <td>${job.title}</td>
                        <td>${job.department}</td>
                        <td>${job.hoursPerWeek}</td>
                        <td>${job.deadline}</td>
                        <td>
                            <span class="badge ${job.status == 'CANCELLED' ? 'text-bg-secondary' : 'text-bg-success'}">
                                ${job.status}
                            </span>
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
            <div class="alert alert-secondary mt-3 mb-0">You have not created any jobs yet.</div>
        </c:if>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
