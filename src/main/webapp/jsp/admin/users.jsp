<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin User Management"/>
<c:set var="pageSection" value="admin-users"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Admin user limit overview"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">Applicant Limit Management</h1>
        <p class="text-muted mb-0">Review applicant usage and configure per-user application limit overrides.</p>
    </div>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/home">Back to admin home</a>
</div>
<div class="card shadow-sm">
    <div class="card-body">
        <c:choose>
            <c:when test="${empty applicantLimitViews}">
                <div class="alert alert-secondary mb-0">No applicant accounts are available.</div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table align-middle">
                        <thead>
                        <tr>
                            <th>User</th>
                            <th>Profile</th>
                            <th>Active Applications</th>
                            <th>Accepted Jobs</th>
                            <th>Effective Limit</th>
                            <th>Source</th>
                            <th>Override</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${applicantLimitViews}" var="view">
                            <tr class="${view.overEffectiveLimit ? 'table-warning' : ''}">
                                <td>
                                    <div class="fw-semibold">${view.user.username}</div>
                                    <div class="small text-muted">${view.user.email}</div>
                                    <div class="small text-muted">${view.user.id}</div>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty view.profile}">
                                            <span class="badge text-bg-secondary">No profile yet</span>
                                        </c:when>
                                        <c:otherwise>
                                            <div>${view.profile.fullName}</div>
                                            <div class="small text-muted">${view.profile.programme}</div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <strong>${view.activeApplicationCount}</strong>
                                    <c:if test="${view.overEffectiveLimit}">
                                        <div class="small text-danger">Above effective limit</div>
                                    </c:if>
                                </td>
                                <td>${view.acceptedAssignmentCount}</td>
                                <td>${view.effectiveApplicationLimit}</td>
                                <td>
                                    <span class="badge ${view.usingOverride ? 'text-bg-primary' : 'text-bg-light'}">
                                        ${view.usingOverride ? 'Override' : 'Global default'}
                                    </span>
                                </td>
                                <td>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/limit" class="needs-validation d-flex gap-2 align-items-center flex-wrap" novalidate>
                                        <input type="hidden" name="applicantUserId" value="${view.user.id}">
                                        <div>
                                            <input
                                                    class="form-control form-control-sm"
                                                    type="number"
                                                    min="0"
                                                    name="applicationLimitOverride"
                                                    value="${view.applicationLimitOverride}"
                                                    placeholder="Use global default"
                                                    style="max-width: 170px;">
                                            <div class="invalid-feedback">Override must be zero or greater.</div>
                                        </div>
                                        <button class="btn btn-sm btn-primary" type="submit">Save</button>
                                        <button class="btn btn-sm btn-outline-secondary" type="submit" name="applicationLimitOverride" value="">Clear</button>
                                    </form>
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
