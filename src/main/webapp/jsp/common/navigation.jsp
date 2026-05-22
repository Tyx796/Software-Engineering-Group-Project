<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<nav class="workspace-nav">
    <c:if test="${sessionScope.currentUser.role == 'APPLICANT'}">
        <a class="workspace-nav-link ${pageSection == 'applicant-jobs' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/applicant/jobs">Browse Jobs</a>
        <a class="workspace-nav-link ${pageSection == 'applicant-applications' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/applicant/applications">My Applications</a>
        <a class="workspace-nav-link ${pageSection == 'applicant-profile' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/applicant/profile">My Profile</a>
        <a class="workspace-nav-link ${pageSection == 'applicant-cv' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/applicant/cv">My CV</a>
        <a class="workspace-nav-link ${pageSection == 'applicant-messages' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/applicant/messages">Messages</a>
    </c:if>
    <c:if test="${sessionScope.currentUser.role == 'ORGANISER'}">
        <a class="workspace-nav-link ${pageSection == 'organiser-jobs' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/organiser/jobs">My Jobs</a>
        <a class="workspace-nav-link ${pageSection == 'organiser-create-job' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/organiser/jobs/create">Create Job</a>
        <a class="workspace-nav-link ${pageSection == 'organiser-messages' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/organiser/messages">Messages</a>
    </c:if>
    <c:if test="${sessionScope.currentUser.role == 'ADMIN'}">
        <a class="workspace-nav-link ${pageSection == 'admin-home' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/home">Dashboard</a>
        <a class="workspace-nav-link ${pageSection == 'admin-settings' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/settings">Settings</a>
        <a class="workspace-nav-link ${pageSection == 'admin-users' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/users">Users</a>
        <a class="workspace-nav-link ${pageSection == 'admin-workloads' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/workloads">Workloads</a>
        <a class="workspace-nav-link ${pageSection == 'admin-jobs' ? 'active' : ''}"
           href="${pageContext.request.contextPath}/admin/jobs">Jobs</a>
    </c:if>
</nav>
