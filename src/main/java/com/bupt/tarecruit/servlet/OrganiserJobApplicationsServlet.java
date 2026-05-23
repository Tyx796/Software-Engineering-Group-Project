package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.service.OrganiserApplicationReviewService;
import com.bupt.tarecruit.service.RecruitmentPolicyService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/organiser/jobs/applications")
public class OrganiserJobApplicationsServlet extends BaseServlet {
    private final JobService jobService = new JobService();
    private final RecruitmentPolicyService recruitmentPolicyService = new RecruitmentPolicyService();
    private final OrganiserApplicationReviewService reviewService = new OrganiserApplicationReviewService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String jobId = request.getParameter("jobId");
        if (jobId == null || jobId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required.");
            return;
        }

        User organiser = SessionUtil.currentUser(request);
        try {
            Job job = jobService.getOwnedJobForOrganiser(organiser.getId(), jobId);
            String statusFilter = request.getParameter("status");
            String keyword = request.getParameter("keyword");
            String sortOption = request.getParameter("sort");

            request.setAttribute("job", job);
            request.setAttribute(
                    "reviewViews",
                    reviewService.getReviewViews(organiser.getId(), jobId, statusFilter, keyword, sortOption));
            request.setAttribute("statusFilter", statusFilter == null ? "" : statusFilter.trim());
            request.setAttribute("keyword", keyword == null ? "" : keyword.trim());
            request.setAttribute("sortOption", sortOption == null || sortOption.isBlank() ? "appliedAt" : sortOption.trim());
            request.setAttribute(
                    "hasActiveFilters",
                    (statusFilter != null && !statusFilter.isBlank()) || (keyword != null && !keyword.isBlank()));
            request.setAttribute("acceptedCount", recruitmentPolicyService.countAcceptedApplications(jobId));
            request.setAttribute("remainingAssistantSlots",
                    recruitmentPolicyService.remainingAssistantSlots(jobId));
            request.setAttribute("jobFull", recruitmentPolicyService.isJobFull(jobId));
            setApplicationStatusView(request);
            forward(request, response, "organiser/job_applications.jsp");
        } catch (IllegalArgumentException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }
}
