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
        request.setAttribute("job", jobService.findById(jobId).orElse(null));
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
        try {
            applicationService.submitApplication(SessionUtil.currentUser(request).getId(), jobId);
            setFlash(request, "Application submitted successfully.");
        } catch (IllegalArgumentException exception) {
            setFlash(request, exception.getMessage());
        }
        redirect(request, response, "/applicant/job-detail?id=" + jobId);
    }
}
