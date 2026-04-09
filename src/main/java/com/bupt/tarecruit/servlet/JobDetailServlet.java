package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.ApplicantService;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/applicant/job-detail")
public class JobDetailServlet extends BaseServlet {
    private final JobService jobService = new JobService();
    private final ApplicantService applicantService = new ApplicantService();
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String jobId = request.getParameter("id");
        if (jobId == null || jobId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required.");
            return;
        }
        var job = jobService.findById(jobId).orElse(null);
        if (job == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Job not found.");
            return;
        }
        request.setAttribute("job", job);
        String currentUserId = SessionUtil.currentUser(request).getId();
        request.setAttribute("profile", applicantService.findByUserId(currentUserId).orElse(null));
        request.setAttribute("existingApplication",
                applicationService.getApplicationsByApplicant(currentUserId).stream()
                        .filter(application -> application.getJobId().equals(jobId))
                        .findFirst()
                        .orElse(null));
        forward(request, response, "applicant/job_detail.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String jobId = request.getParameter("id");
        if (jobId == null || jobId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required.");
            return;
        }
        try {
            applicationService.submitApplication(SessionUtil.currentUser(request).getId(), jobId);
            setFlash(request, "Application submitted successfully.");
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            doGet(request, response);
            return;
        }
        redirect(request, response, "/applicant/job-detail?id=" + jobId);
    }
}
