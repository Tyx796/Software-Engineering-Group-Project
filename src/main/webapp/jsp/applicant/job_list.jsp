<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Available Jobs"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Applicant job availability"/>
<%@ include file="../common/header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">Available TA jobs</h1>
        <p class="text-muted mb-0">Browse current openings published by organisers.</p>
    </div>
    <div class="d-flex gap-2">
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/applicant/applications">My applications</a>
        <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/applicant/profile">My profile</a>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/cv">My CV</a>
    </div>
</div>
<div class="card shadow-sm mb-4">
    <div class="card-body">
        <form method="get" action="${pageContext.request.contextPath}/applicant/jobs" class="row g-3 align-items-end">
            <div class="col-lg-9">
                <label class="form-label mb-1" for="keyword">Keyword search</label>
                <input class="form-control" id="keyword" type="text" name="keyword" value="${searchKeyword}"
                       placeholder="Search by title, department, or requirement">
            </div>
            <div class="col-lg-3 d-flex gap-2">
                <button class="btn btn-primary flex-grow-1" type="submit">Search</button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/jobs">Reset</a>
            </div>
        </form>
    </div>
</div>
<c:if test="${not empty recommendedJobs}">
    <div class="card shadow-sm mb-4 border-primary-subtle">
        <div class="card-body">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <div>
                    <h2 class="h5 mb-1">Recommended for you</h2>
                    <p class="text-muted small mb-0">Based on your current profile skills and open TA jobs.</p>
                </div>
            </div>
            <div class="row g-3">
                <c:forEach items="${recommendedJobs}" var="recommendation">
                    <div class="col-lg-4">
                        <div class="border rounded h-100 p-3">
                            <div class="d-flex justify-content-between align-items-start gap-2 mb-2">
                                <div>
                                    <div class="fw-semibold">${recommendation.job.title}</div>
                                    <div class="small text-muted">${recommendation.job.department}</div>
                                </div>
                                <span class="badge ${recommendation.matchScore >= 75 ? 'text-bg-success' : recommendation.matchScore >= 40 ? 'text-bg-warning' : 'text-bg-secondary'}">
                                    ${recommendation.matchScore}% match
                                </span>
                            </div>
                            <div class="small text-muted mb-3">Deadline ${recommendation.job.deadline}</div>
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/applicant/job-detail?id=${recommendation.job.id}">
                                View recommendation
                            </a>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</c:if>
<div class="row g-3 mb-4">
    <div class="col-md-4">
        <div class="card shadow-sm stat-card"><div class="card-body"><div class="small text-muted">Open jobs</div><div class="display-6 fw-semibold">${jobs.size()}</div></div></div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm stat-card"><div class="card-body"><div class="small text-muted">Active applications</div><div class="h4 mb-0">${activeApplicationCount} / ${effectiveApplicationLimit}</div></div></div>
    </div>
    <div class="col-md-4">
        <div class="card shadow-sm stat-card"><div class="card-body"><div class="small text-muted">CV status</div><div class="h4 mb-0">${hasUploadedCv ? 'Uploaded' : 'Missing'}</div></div></div>
    </div>
</div>
<div class="alert ${hasReachedApplicationLimit ? 'alert-warning' : 'alert-info'}">
    You can keep up to <strong>${effectiveApplicationLimit}</strong> active applications.
    Current usage: <strong>${activeApplicationCount}</strong>.
    Contact Admin if you need an adjustment.
</div>
<c:if test="${empty profile}">
    <div class="alert alert-warning">Complete your profile before uploading a CV or applying for jobs.</div>
</c:if>
<c:if test="${not empty searchKeyword}">
    <div class="alert alert-light border">Showing search results for <strong>${searchKeyword}</strong>.</div>
</c:if>
<div class="row g-4">
    <c:forEach items="${jobs}" var="job">
        <div class="col-md-6 col-xl-4">
            <div class="card h-100 shadow-sm job-card">
                <div class="card-body d-flex flex-column">
                    <h2 class="h5">${job.title}</h2>
                    <p class="text-muted mb-2">${job.department}</p>
                    <p class="small flex-grow-1">${job.description}</p>
                    <p class="mb-2"><strong>Hours/week:</strong> ${job.hoursPerWeek}</p>
                    <p class="mb-3"><strong>Deadline:</strong> ${job.deadline}</p>
                    <p class="mb-2"><strong>Filled slots:</strong> ${acceptedCountsByJobId[job.id]} / ${job.assistantQuota}</p>
                    <p class="mb-3">
                        <strong>Remaining:</strong> ${remainingSlotsByJobId[job.id]}
                        <span class="badge ${fullJobsById[job.id] ? 'text-bg-warning' : 'text-bg-success'} ms-2">
                            ${fullJobsById[job.id] ? 'Full' : 'Open'}
                        </span>
                    </p>
                    <div class="mb-3">
                        <c:forEach items="${job.requirements}" var="requirement">
                            <span class="badge text-bg-light me-1 mb-1">${requirement}</span>
                        </c:forEach>
                    </div>
                    <a class="btn btn-primary mt-auto" href="${pageContext.request.contextPath}/applicant/job-detail?id=${job.id}">View details</a>
                </div>
            </div>
        </div>
    </c:forEach>
</div>
<c:if test="${empty jobs}">
    <div class="alert alert-secondary">No open jobs are available yet.</div>
</c:if>
<%@ include file="../common/footer.jsp" %>
