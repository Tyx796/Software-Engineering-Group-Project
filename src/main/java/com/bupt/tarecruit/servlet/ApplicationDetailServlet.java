package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.service.RecruitmentPolicyService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/applicant/applications/detail")
public class ApplicationDetailServlet extends BaseServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();
    private final RecruitmentPolicyService recruitmentPolicyService = new RecruitmentPolicyService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String applicationId = request.getParameter("id");
        if (applicationId == null || applicationId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application ID is required.");
            return;
        }

        Application application = applicationService.getApplicationDetails(applicationId).orElse(null);
        if (application == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found.");
            return;
        }
        if (!application.getApplicantUserId().equals(SessionUtil.currentUser(request).getId())) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Application not found.");
            return;
        }

        request.setAttribute("application", application);
        var job = jobService.findById(application.getJobId()).orElse(null);
        request.setAttribute("job", job);
        request.setAttribute("activeApplicationCount",
                recruitmentPolicyService.countActiveApplications(application.getApplicantUserId()));
        request.setAttribute("effectiveApplicationLimit",
                recruitmentPolicyService.resolveApplicantApplicationLimit(application.getApplicantUserId()));
        if (job != null) {
            request.setAttribute("acceptedCount", recruitmentPolicyService.countAcceptedApplications(job.getId()));
            request.setAttribute("remainingAssistantSlots",
                    recruitmentPolicyService.remainingAssistantSlots(job.getId()));
            request.setAttribute("jobFull", recruitmentPolicyService.isJobFull(job.getId()));
        }
        setApplicationStatusView(request);
        forward(request, response, "applicant/application_detail.jsp");
    }
}