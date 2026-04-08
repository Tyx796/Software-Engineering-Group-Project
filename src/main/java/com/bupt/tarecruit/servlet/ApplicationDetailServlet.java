package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;
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
        request.setAttribute("job", jobService.findById(application.getJobId()).orElse(null));
        forward(request, response, "applicant/application_detail.jsp");
    }
}
