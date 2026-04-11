package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.ApplicationStatus;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@WebServlet("/organiser/applications/status")
public class OrganiserApplicationStatusServlet extends BaseServlet {
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String applicationId = request.getParameter("applicationId");
        String statusValue = request.getParameter("status");
        if (applicationId == null || applicationId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application ID is required.");
            return;
        }
        if (statusValue == null || statusValue.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application status is required.");
            return;
        }

        User organiser = SessionUtil.currentUser(request);
        try {
            ApplicationStatus status = ApplicationStatus.valueOf(statusValue.trim().toUpperCase(Locale.ROOT));
            applicationService.updateStatusForOrganiser(organiser.getId(), applicationId, status);
            setFlash(request, "Application status updated to " + status + ".");
        } catch (IllegalArgumentException exception) {
            setFlash(request, exception.getMessage());
        }
        redirect(request, response, "/organiser/applications/detail?id=" + applicationId);
    }
}
