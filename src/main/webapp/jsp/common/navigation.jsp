<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="mb-4">
    <ul class="nav nav-pills flex-column flex-md-row gap-2">
        <c:if test="${sessionScope.currentUser.role == 'APPLICANT'}">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/applicant/jobs">Browse Jobs</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/applicant/applications">My Applications</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/applicant/profile">My Profile</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/applicant/cv">My CV</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/applicant/messages">Messages</a>
            </li>
        </c:if>
        <c:if test="${sessionScope.currentUser.role == 'ORGANISER'}">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/organiser/jobs">My Jobs</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/organiser/jobs/create">Create Job</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/organiser/messages">Messages</a>
            </li>
        </c:if>
        <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
            <li class="nav-item">
                <a class="nav-link" href="${pageContext.request.contextPath}/admin/home">Admin Home</a>
            </li>
        </c:if>
    </ul>
</nav>
