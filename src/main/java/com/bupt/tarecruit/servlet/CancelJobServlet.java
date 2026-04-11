package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/organiser/jobs/cancel")
public class CancelJobServlet extends BaseServlet {
    private final JobService jobService = new JobService();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String jobId = request.getParameter("jobId");
        if (jobId == null || jobId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required.");
            return;
        }

        try {
            jobService.cancelJobForOrganiser(SessionUtil.currentUser(request).getId(), jobId);
            setFlash(request, "Job cancelled successfully.");
        } catch (IllegalArgumentException exception) {
            setFlash(request, exception.getMessage());
        }
        redirect(request, response, "/organiser/jobs");
    }
}
