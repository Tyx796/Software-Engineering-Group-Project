<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Job Detail"/>
<c:set var="pageSection" value="applicant-jobs"/>
<c:set var="pageAutoRefreshSeconds" value="30"/>
<c:set var="pageAutoRefreshLabel" value="Job detail quota status"/>
<%@ include file="../common/header.jsp" %>
<c:choose>
    <c:when test="${empty job}">
        <div class="alert alert-danger">Job not found.</div>
    </c:when>
    <c:otherwise>
        <a class="btn btn-link px-0 mb-3" href="${pageContext.request.contextPath}/applicant/jobs">&larr; Back to jobs</a>
        <section class="page-hero detail-hero">
            <div class="hero-copy">
                <div class="section-kicker">Role detail</div>
                <h1 class="h2 mb-2">${job.title}</h1>
                <p class="mb-0">${job.department} · ${job.hoursPerWeek} hours/week · Deadline ${job.deadline}</p>
            </div>
            <div class="hero-actions hero-actions-compact">
                <span class="status-chip ${jobFull ? 'is-pending' : 'is-ready'}">${jobFull ? 'Job full' : 'Open for review'}</span>
                <span class="status-chip ${empty existingApplication ? 'is-pending' : 'is-ready'}">
                    <c:choose>
                        <c:when test="${empty existingApplication}">Not applied</c:when>
                        <c:otherwise>${existingApplication.status}</c:otherwise>
                    </c:choose>
                </span>
            </div>
        </section>
        <div class="detail-grid">
            <section class="detail-panel">
                <div class="workspace-card detail-story-card">
                    <div class="card-body">
                        <p class="detail-lead">${job.description}</p>
                        <div class="job-data detail-facts">
                            <div class="job-data-item">
                                <div class="job-data-label">Filled slots</div>
                                <strong>${acceptedCount} / ${job.assistantQuota}</strong>
                            </div>
                            <div class="job-data-item">
                                <div class="job-data-label">Remaining</div>
                                <strong>${remainingAssistantSlots}</strong>
                            </div>
                            <div class="job-data-item">
                                <div class="job-data-label">Application usage</div>
                                <strong>${activeApplicationCount} / ${effectiveApplicationLimit}</strong>
                            </div>
                        </div>
                        <h2 class="h5 mt-4 mb-3">Requirements</h2>
                        <div class="job-tags">
                            <c:forEach items="${job.requirements}" var="requirement">
                                <span class="badge text-bg-light">${requirement}</span>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </section>
            <aside class="detail-sidebar">
                <div class="workspace-card readiness-card">
                    <div class="card-body">
                        <div class="section-kicker mb-2">Apply with confidence</div>
                        <h2 class="h5 mb-3">Application readiness</h2>
                        <ul class="list-unstyled small readiness-points">
                            <li><span>Profile</span><strong>${empty profile ? 'Missing' : 'Ready'}</strong></li>
                            <li><span>CV</span><strong>${hasUploadedCv ? 'Uploaded' : 'Missing'}</strong></li>
                            <li><span>Slots</span><strong>${acceptedCount} / ${job.assistantQuota}</strong></li>
                            <li><span>Remaining</span><strong>${remainingAssistantSlots}</strong></li>
                            <li>
                                <span>Status</span>
                                <strong>
                                    <c:choose>
                                        <c:when test="${empty existingApplication}">Not applied</c:when>
                                        <c:otherwise>
                                            <span class="badge app-status ${applicationStatusBadgeClasses[existingApplication.status]}">
                                                ${existingApplication.status}
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </strong>
                            </li>
                        </ul>
                        <div class="focus-panel">
                            <div class="focus-panel-art" aria-hidden="true"></div>
                            <div class="focus-panel-copy">
                                <h3 class="h6 mb-2">Skill match</h3>
                                <c:choose>
                                    <c:when test="${empty profile}">
                                        <div class="text-muted">Complete your profile to see skill match insights before applying.</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="mb-3">
                                            <strong>Matched skills:</strong>
                                            <c:choose>
                                                <c:when test="${empty skillMatch.matchedSkills}">
                                                    <span class="text-muted">None yet</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="job-tags mt-2">
                                                        <c:forEach items="${skillMatch.matchedSkills}" var="matchedSkill">
                                                            <span class="badge text-bg-success">${matchedSkill}</span>
                                                        </c:forEach>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="mb-3">
                                            <strong>Missing skills:</strong>
                                            <c:choose>
                                                <c:when test="${empty skillMatch.missingSkills}">
                                                    <span class="text-muted">No missing required skills.</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="job-tags mt-2">
                                                        <c:forEach items="${skillMatch.missingSkills}" var="missingSkill">
                                                            <span class="badge text-bg-warning">${missingSkill}</span>
                                                        </c:forEach>
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div class="text-muted">You can still apply even if some required skills are missing.</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <c:choose>
                            <c:when test="${not empty existingApplication}">
                                <div class="alert alert-light border small mt-3">
                                    <div class="fw-semibold mb-1">You already applied for this job.</div>
                                    <div>${applicationStatusSummaries[existingApplication.status]}</div>
                                </div>
                                <a class="btn btn-outline-primary w-100"
                                   href="${pageContext.request.contextPath}/applicant/applications/detail?id=${existingApplication.id}">
                                    View application
                                </a>
                            </c:when>
                            <c:otherwise>
                                <form method="post" action="${pageContext.request.contextPath}/applicant/job-detail" class="d-grid gap-2 mt-3">
                                    <input type="hidden" name="id" value="${job.id}">
                                    <c:if test="${jobFull}">
                                        <div class="alert alert-warning small mb-0">
                                            This job is full. No more applications can be accepted right now.
                                        </div>
                                    </c:if>
                                    <c:if test="${hasReachedApplicationLimit}">
                                        <div class="alert alert-warning small mb-0">
                                            You have reached your application limit. Contact Admin if you need an adjustment.
                                        </div>
                                    </c:if>
                                    <button class="btn btn-primary" type="submit" ${jobFull || hasReachedApplicationLimit ? 'disabled' : ''}>Apply now</button>
                                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/profile">Update profile</a>
                                    <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/applicant/cv">Manage CV</a>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </aside>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="../common/footer.jsp" %>
