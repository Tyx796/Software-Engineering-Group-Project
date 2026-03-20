<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create Job"/>
<%@ include file="../common/header.jsp" %>
<div class="row justify-content-center">
    <div class="col-lg-8">
        <div class="card shadow-sm">
            <div class="card-body">
                <h1 class="h3 mb-3">Create job posting</h1>
                <form method="post" action="${pageContext.request.contextPath}/organiser/jobs/create">
                    <div class="mb-3">
                        <label class="form-label">Title</label>
                        <input class="form-control" type="text" name="title" required value="${param.title}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Department</label>
                        <input class="form-control" type="text" name="department" required value="${param.department}">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea class="form-control" name="description" rows="5" required>${param.description}</textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Requirements</label>
                        <textarea class="form-control" name="requirements" rows="4" required>${param.requirements}</textarea>
                        <div class="form-text">Use commas or new lines to separate skills and requirements.</div>
                    </div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label class="form-label">Hours per week</label>
                            <input class="form-control" type="number" name="hoursPerWeek" min="1" required value="${param.hoursPerWeek}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Deadline</label>
                            <input class="form-control" type="date" name="deadline" required value="${param.deadline}">
                        </div>
                    </div>
                    <button class="btn btn-primary mt-3" type="submit">Publish job</button>
                </form>
            </div>
        </div>
    </div>
</div>
<%@ include file="../common/footer.jsp" %>
