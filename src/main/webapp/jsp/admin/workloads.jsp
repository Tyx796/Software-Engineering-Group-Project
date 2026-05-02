<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Workloads"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Admin workload monitoring"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
  <div>
    <h1 class="h3 mb-1">Workload Monitoring</h1>
    <p class="text-muted mb-0">Review accepted assignments and flag applicants whose weekly hours exceed the threshold.</p>
  </div>
  <div class="d-flex gap-2">
    <a class="btn btn-outline-primary"
       href="${pageContext.request.contextPath}/admin/workloads/export?threshold=${workloadThreshold}">
      Export to CSV
    </a>
    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/home">Back to dashboard</a>
  </div>
</div>
<div class="card shadow-sm mb-4">
  <div class="card-body">
    <form method="get" action="${pageContext.request.contextPath}/admin/workloads" class="needs-validation row gy-2 gx-3 align-items-end" novalidate>
      <div class="col-sm-4 col-lg-3">
        <label class="form-label" for="threshold">Workload threshold (hours/week)</label>
        <input class="form-control" id="threshold" type="number" min="1" name="threshold" value="${workloadThreshold}" required>
        <div class="invalid-feedback">Workload threshold must be greater than zero.</div>
      </div>
      <div class="col-auto">
        <button class="btn btn-primary" type="submit">Refresh</button>
      </div>
    </form>
  </div>
</div>
<div class="card shadow-sm">
  <div class="card-body">
    <c:choose>
      <c:when test="${empty workloadViews}">
        <div class="alert alert-secondary mb-0">No applicant accounts are available.</div>
      </c:when>
      <c:otherwise>
        <div class="table-responsive">
          <table class="table align-middle">
            <thead>
            <tr>
              <th>Applicant</th>
              <th>Profile</th>
              <th>Accepted Jobs</th>
              <th>Hours/Week</th>
              <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${workloadViews}" var="view">
              <tr class="${view.overloaded ? 'table-warning' : ''}">
                <td>
                  <div class="fw-semibold">${view.user.username}</div>
                  <div class="small text-muted">${view.user.email}</div>
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
                  <c:choose>
                    <c:when test="${empty view.acceptedAssignments}">
                      <span class="text-muted">None</span>
                    </c:when>
                    <c:otherwise>
                      <c:forEach items="${view.acceptedAssignments}" var="assignment">
                        <div class="mb-2">
                          <div class="fw-semibold">${assignment.title}</div>
                          <div class="small text-muted">
                              ${assignment.department} · ${assignment.hoursPerWeek} hours/week
                          </div>
                        </div>
                      </c:forEach>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td>
                  <strong>${view.totalHoursPerWeek}</strong>
                  <div class="small text-muted">${view.workloadAlertMessage}</div>
                </td>
                <td>
                                    <span class="badge ${view.overloaded ? 'text-bg-warning' : 'text-bg-success'}">
                                        ${view.workloadStatusLabel}
                                    </span>
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
