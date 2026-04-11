<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${empty formHeading ? 'Job Form' : formHeading}"/>
<%@ include file="../common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body">
                <h1 class="h3 mb-3">${formHeading}</h1>
                <form method="post" action="${formAction}" class="needs-validation" novalidate>
                    <c:if test="${not empty jobId}">
                        <input type="hidden" name="jobId" value="${jobId}">
                    </c:if>
                    <div class="mb-3">
                        <label class="form-label">Title</label>
                        <input class="form-control" type="text" name="title" required value="${formTitle}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Department</label>
                        <input class="form-control" type="text" name="department" required value="${formDepartment}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="5" required>${formDescription}</textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Requirements</label>
                        <textarea class="form-control" name="requirements" rows="4" required>${formRequirements}</textarea>
                        <div class="form-text">Use commas or new lines to separate skills and requirements.</div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Hours per week</label>
                            <input class="form-control" type="number" name="hoursPerWeek" min="1" required value="${formHoursPerWeek}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Deadline</label>
                            <input class="form-control" type="date" name="deadline" min="${today}" required value="${formDeadline}">
                        </div>
                    </div>
                    <button class="btn btn-primary mt-3" type="submit">${submitLabel}</button>
                    <a class="btn btn-outline-secondary mt-3 ms-2" href="${pageContext.request.contextPath}/organiser/jobs">Back to jobs</a>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
