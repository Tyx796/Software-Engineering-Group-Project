package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Applicant;
import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.ApplicantService;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.CvService;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/organiser/applications/detail")
public class OrganiserApplicationDetailServlet extends BaseServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final ApplicantService applicantService = new ApplicantService();
    private final JobService jobService = new JobService();
    private final CvService cvService = new CvService(applicantService);

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String applicationId = request.getParameter("id");
        if (applicationId == null || applicationId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application ID is required.");
            return;
        }

        User organiser = SessionUtil.currentUser(request);
        try {
            Application application = applicationService.openApplicationForOrganiser(organiser.getId(), applicationId);
            Applicant applicant = applicantService.findByUserId(application.getApplicantUserId()).orElse(null);
            Job job = jobService.findById(application.getJobId()).orElse(null);

            request.setAttribute("application", application);
            request.setAttribute("applicant", applicant);
            request.setAttribute("job", job);
            request.setAttribute("hasUploadedCv", cvService.hasUploadedCv(application.getApplicantUserId()));
            request.setAttribute("currentCvFileName", cvService.currentCvFileName(application.getApplicantUserId()).orElse(""));
            forward(request, response, "organiser/application_detail.jsp");
        } catch (IllegalArgumentException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }
}
