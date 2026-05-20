<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Available Jobs"/>
<c:set var="pageSection" value="applicant-jobs"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Applicant job availability"/>
<%@ include file="../common/header.jsp" %>
<section class="page-hero applicant-hero">
    <div class="hero-copy">
        <div class="section-kicker">Applicant Workspace</div>
        <h1 class="h1 mb-3">Available TA jobs</h1>
        <p class="mb-0">Focus on the roles you can act on now, keep your application load visible, and use match signals without wading through dashboard noise.</p>
    </div>
    <div class="hero-actions">
        <a class="btn btn-dark" href="${pageContext.request.contextPath}/applicant/applications">My applications</a>
        <a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/applicant/profile">My profile</a>
        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/cv">Manage CV</a>
    </div>
</section>

<section class="application-capacity-bar">
    <div class="capacity-copy">
        <span class="capacity-label">Application capacity</span>
        <strong>${activeApplicationCount} / ${effectiveApplicationLimit}</strong>
        <span class="capacity-note">
            ${hasReachedApplicationLimit ? 'You are at your current active application limit.' : 'You still have room to apply to more roles.'}
        </span>
    </div>
    <div class="capacity-progress-track" aria-hidden="true">
        <div class="capacity-progress-fill ${hasReachedApplicationLimit ? 'is-warning' : ''}"
             style="width: ${(activeApplicationCount * 100) / (effectiveApplicationLimit == 0 ? 1 : effectiveApplicationLimit)}%;"></div>
    </div>
    <div class="capacity-meta">
        <span class="status-chip ${hasUploadedCv ? 'is-ready' : 'is-pending'}">CV ${hasUploadedCv ? 'ready' : 'missing'}</span>
        <span class="status-chip ${empty profile ? 'is-pending' : 'is-ready'}">Profile ${empty profile ? 'incomplete' : 'ready'}</span>
    </div>
</section>

<section class="workspace-card filter-card filter-card-quiet">
    <div class="card-body">
        <form method="get" action="${pageContext.request.contextPath}/applicant/jobs" class="row g-3 align-items-end jobs-toolbar search-inline">
            <div class="col-lg-9">
                <label class="form-label mb-1" for="keyword">Search jobs</label>
                <input class="form-control" id="keyword" type="text" name="keyword" value="${searchKeyword}"
                       placeholder="Search by title, department, or requirement">
            </div>
            <div class="col-lg-3 d-flex gap-2">
                <button class="btn btn-primary flex-grow-1" type="submit">Search</button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/jobs">Reset</a>
            </div>
        </form>
    </div>
</section>

<section class="applicant-strip">
    <div class="strip-item">
        <span class="strip-label">Open roles</span>
        <strong>${jobs.size()}</strong>
        <span>Current opportunities published by organisers.</span>
    </div>
    <div class="strip-item">
        <span class="strip-label">Recommended view</span>
        <strong>${empty recommendedJobs ? 'No matches yet' : 'Skill-based shortlist'}</strong>
        <span>Signals are based on your current profile and each role's requirements.</span>
    </div>
    <div class="strip-item">
        <span class="strip-label">Readiness</span>
        <strong>${empty profile ? 'Profile needed' : 'Ready to refine'}</strong>
        <span>${empty profile ? 'Complete your profile before uploading a CV or applying for jobs.' : 'Keep your profile and CV current before applying.'}</span>
    </div>
</section>

<c:if test="${not empty recommendedJobs}">
    <section class="jobs-highlight-grid">
        <div class="workspace-card jobs-highlight-card recommendation-hero-card">
            <div class="card-body">
                <div class="insight-panel-header">
                    <div>
                        <div class="section-kicker mb-2">Signals</div>
                        <h2 class="section-title">Recommended for you</h2>
                        <p class="text-muted mb-0">A smaller, sharper shortlist based on your current profile skills and open TA jobs.</p>
                    </div>
                </div>
                <div class="recommendation-layout">
                    <c:forEach items="${recommendedJobs}" var="recommendation" varStatus="status">
                        <c:choose>
                            <c:when test="${status.first}">
                                <article class="recommendation-featured">
                                    <div class="recommendation-badge">
                                        <span class="badge ${recommendation.matchScore >= 75 ? 'text-bg-success' : recommendation.matchScore >= 40 ? 'text-bg-warning' : 'text-bg-secondary'}">
                                            ${recommendation.matchScore}% match
                                        </span>
                                    </div>
                                    <div class="recommendation-content">
                                        <div class="section-kicker">Best current fit</div>
                                        <h3 class="job-title">${recommendation.job.title}</h3>
                                        <div class="job-meta">${recommendation.job.department}</div>
                                        <p class="job-copy">Deadline ${recommendation.job.deadline} · ${recommendation.job.hoursPerWeek} hours/week · ${remainingSlotsByJobId[recommendation.job.id]} slots left.</p>
                                        <div class="job-tags is-muted">
                                            <c:forEach items="${recommendation.skillMatch.matchedSkills}" var="matchedSkill">
                                                <span class="badge text-bg-light">${matchedSkill}</span>
                                            </c:forEach>
                                        </div>
                                        <a class="btn btn-primary mt-auto"
                                           href="${pageContext.request.contextPath}/applicant/job-detail?id=${recommendation.job.id}">
                                            View recommendation
                                        </a>
                                    </div>
                                </article>
                            </c:when>
                        </c:choose>
                    </c:forEach>
                    <div class="recommendation-stack">
                        <c:forEach items="${recommendedJobs}" var="recommendation" varStatus="status">
                            <c:if test="${not status.first}">
                                <article class="recommendation-mini">
                                    <div>
                                        <h3 class="job-title">${recommendation.job.title}</h3>
                                        <div class="job-meta">${recommendation.job.department}</div>
                                    </div>
                                    <div class="recommendation-mini-meta">
                                        <span class="badge ${recommendation.matchScore >= 75 ? 'text-bg-success' : recommendation.matchScore >= 40 ? 'text-bg-warning' : 'text-bg-secondary'}">
                                            ${recommendation.matchScore}% match
                                        </span>
                                        <a class="btn btn-sm btn-outline-primary"
                                           href="${pageContext.request.contextPath}/applicant/job-detail?id=${recommendation.job.id}">
                                            View
                                        </a>
                                    </div>
                                </article>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </section>
</c:if>

<section class="jobs-section-header">
    <div>
        <div class="section-kicker">Open roles</div>
        <h2 class="section-title mb-1">Browse all current listings</h2>
        <p class="text-muted mb-0">View the essentials first, then open the full detail page when a role looks promising.</p>
    </div>
    <c:if test="${not empty searchKeyword}">
        <div class="status-prompt">Showing search results for <strong>${searchKeyword}</strong>.</div>
    </c:if>
</section>

<div class="job-grid">
    <c:forEach items="${jobs}" var="job">
        <div>
            <article class="job-surface">
                <div class="job-header">
                    <div>
                        <h2 class="job-title">${job.title}</h2>
                        <div class="job-meta">${job.department}</div>
                    </div>
                    <span class="badge ${fullJobsById[job.id] ? 'text-bg-warning' : 'text-bg-success'}">
                        ${fullJobsById[job.id] ? 'Full' : 'Open'}
                    </span>
                </div>
                <p class="job-copy">${job.description}</p>
                <div class="job-data job-data-compact">
                    <div class="job-data-item">
                        <div class="job-data-label">Deadline</div>
                        <strong>${job.deadline}</strong>
                    </div>
                    <div class="job-data-item">
                        <div class="job-data-label">Hours / week</div>
                        <strong>${job.hoursPerWeek}</strong>
                    </div>
                    <div class="job-data-item">
                        <div class="job-data-label">Remaining</div>
                        <strong>${remainingSlotsByJobId[job.id]}</strong>
                    </div>
                </div>
                <div class="job-tags job-tags-secondary">
                    <c:forEach items="${job.requirements}" var="requirement">
                        <span class="badge text-bg-light">${requirement}</span>
                    </c:forEach>
                </div>
                <div class="job-actions">
                    <span class="job-footnote">Filled slots ${acceptedCountsByJobId[job.id]} / ${job.assistantQuota}</span>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/applicant/job-detail?id=${job.id}">View details</a>
                </div>
            </article>
        </div>
    </c:forEach>
</div>

<c:if test="${empty jobs}">
    <div class="editorial-empty-state mt-4">
        <div class="editorial-empty-art"></div>
        <div class="editorial-empty-copy">
            <div class="section-kicker">No jobs right now</div>
            <h2 class="section-title">No open jobs are available yet.</h2>
            <p class="text-muted mb-0">Check back later or refine your search terms to see more roles.</p>
        </div>
    </div>
</c:if>
<%@ include file="../common/footer.jsp" %>
