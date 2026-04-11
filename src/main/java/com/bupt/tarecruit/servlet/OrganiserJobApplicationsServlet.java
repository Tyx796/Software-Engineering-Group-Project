package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.ApplicantService;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet("/organiser/jobs/applications")
public class OrganiserJobApplicationsServlet extends BaseServlet {
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final ApplicantService applicantService = new ApplicantService();

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
            List<Application> applications = applicationService.getApplicationsForOrganiserJob(organiser.getId(), jobId);
            Map<String, Applicant> applicantsByUserId = applicantService.getAllProfiles().stream()
                    .collect(Collectors.toMap(Applicant::getUserId, Function.identity(), (left, right) -> right));

            request.setAttribute("job", job);
            request.setAttribute("applications", applications);
            request.setAttribute("applicantsByUserId", applicantsByUserId);
            forward(request, response, "organiser/job_applications.jsp");
        } catch (IllegalArgumentException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }
}
