package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet("/applicant/applications")
public class MyApplicationsServlet extends BaseServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String userId = SessionUtil.currentUser(request).getId();
        var applications = applicationService.getApplicationsByApplicant(userId).stream()
                .sorted(Comparator.comparing(Application::getAppliedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
        Map<String, Job> jobsById = jobService.getAllJobs().stream()
                .collect(Collectors.toMap(Job::getId, Function.identity(), (left, right) -> left));
        request.setAttribute("applications", applications);
        request.setAttribute("jobsById", jobsById);
        setApplicationStatusView(request);
        forward(request, response, "applicant/applications.jsp");
    }
}