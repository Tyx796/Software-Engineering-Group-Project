<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Messages"/>
<%@ include file="header.jsp" %>
<div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
    <div>
        <h1 class="h3 mb-1">Messages</h1>
        <p class="text-muted mb-0">System notifications related to your applications and job postings.</p>
    </div>
</div>
<c:choose>
    <c:when test="${empty messages}">
        <div class="alert alert-secondary">You do not have any messages yet.</div>
    </c:when>
    <c:otherwise>
        <div class="d-grid gap-3">
            <c:forEach items="${messages}" var="message">
                <c:set var="sender" value="${usersById[message.senderUserId]}"/>
                <c:set var="job" value="${jobsById[message.relatedJobId]}"/>
                <div class="card shadow-sm">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-2">
                            <div>
                                <div class="fw-semibold">${message.subject}</div>
                                <div class="small text-muted">
                                    From ${empty sender ? message.senderUserId : sender.username}
                                    • ${message.createdAt}
                                </div>
                            </div>
                            <span class="badge ${message.read ? 'text-bg-light' : 'text-bg-primary'}">
                                ${message.read ? 'Read' : 'Unread'}
                            </span>
                        </div>
                        <p class="mb-2">${message.content}</p>
                        <div class="small text-muted mb-3">
                            <c:if test="${not empty message.relatedJobId}">
                                <div>
                                    Related job:
                                    <c:choose>
                                        <c:when test="${empty job}">
                                            ${message.relatedJobId}
                                        </c:when>
                                        <c:otherwise>
                                            ${job.title}
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:if>
                            <c:if test="${not empty message.relatedApplicationId}">
                                <div>Related application: ${message.relatedApplicationId}</div>
                            </c:if>
                        </div>
                        <div class="d-flex flex-wrap gap-2">
                            <c:if test="${not empty message.relatedApplicationId}">
                                <c:choose>
                                    <c:when test="${sessionScope.currentUser.role == 'APPLICANT'}">
                                        <a class="btn btn-sm btn-outline-primary"
                                           href="${pageContext.request.contextPath}/applicant/applications/detail?id=${message.relatedApplicationId}">
                                            View application
                                        </a>
                                    </c:when>
                                    <c:when test="${sessionScope.currentUser.role == 'ORGANISER'}">
                                        <a class="btn btn-sm btn-outline-primary"
                                           href="${pageContext.request.contextPath}/organiser/applications/detail?id=${message.relatedApplicationId}">
                                            View application
                                        </a>
                                    </c:when>
                                </c:choose>
                            </c:if>
                            <c:if test="${not message.read}">
                                <form method="post"
                                      action="${pageContext.request.contextPath}/${sessionScope.currentUser.role == 'APPLICANT' ? 'applicant' : 'organiser'}/messages/read"
                                      class="m-0">
                                    <input type="hidden" name="messageId" value="${message.id}">
                                    <button class="btn btn-sm btn-outline-secondary" type="submit">Mark as read</button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="footer.jsp" %>
