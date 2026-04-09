package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.ApplicantService;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/applicant/jobs")
public class JobListServlet extends BaseServlet {
    private final JobService jobService = new JobService();
    private final ApplicantService applicantService = new ApplicantService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        request.setAttribute("jobs", jobService.searchAvailableJobs(keyword));
        request.setAttribute("searchKeyword", keyword == null ? "" : keyword.trim());
        request.setAttribute("profile", applicantService.findByUserId(SessionUtil.currentUser(request).getId()).orElse(null));
        forward(request, response, "applicant/job_list.jsp");
    }
}
