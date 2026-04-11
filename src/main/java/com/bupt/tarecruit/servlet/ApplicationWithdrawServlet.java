package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/applicant/applications/withdraw")
public class ApplicationWithdrawServlet extends BaseServlet {
    private final ApplicationService applicationService = new ApplicationService();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String applicationId = request.getParameter("applicationId");
        String returnTo = request.getParameter("returnTo");
        if (applicationId == null || applicationId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application ID is required.");
            return;
        }

        try {
            applicationService.withdrawApplicationByApplicant(SessionUtil.currentUser(request).getId(), applicationId);
            setFlash(request, "Application withdrawn successfully.");
        } catch (IllegalArgumentException exception) {
            setFlash(request, exception.getMessage());
        }

        if ("detail".equals(returnTo)) {
            redirect(request, response, "/applicant/applications/detail?id=" + applicationId);
            return;
        }
        redirect(request, response, "/applicant/applications");
    }
}