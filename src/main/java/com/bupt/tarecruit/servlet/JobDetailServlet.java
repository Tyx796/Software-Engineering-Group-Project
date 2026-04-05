package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.ApplicantService;
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

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String jobId = request.getParameter("id");
        request.setAttribute("job", jobService.findById(jobId).orElse(null));
        request.setAttribute("profile", applicantService.findByUserId(SessionUtil.currentUser(request).getId()).orElse(null));
        forward(request, response, "applicant/job_detail.jsp");
    }
}
