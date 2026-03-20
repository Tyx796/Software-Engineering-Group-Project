package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/organiser/jobs")
public class OrganiserJobsServlet extends BaseServlet {
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        request.setAttribute("jobs", jobService.getJobsByOrganiser(user.getId()));
        forward(request, response, "organiser/job_list.jsp");
    }
}
