package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.ApplicantService;
import com.bupt.tarecruit.service.CvService;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.service.RecruitmentPolicyService;
import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/applicant/jobs")
public class JobListServlet extends BaseServlet {
    private final JobService jobService = new JobService();
    private final ApplicantService applicantService = new ApplicantService();
    private final CvService cvService = new CvService();
    private final RecruitmentPolicyService recruitmentPolicyService = new RecruitmentPolicyService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String currentUserId = SessionUtil.currentUser(request).getId();
        var jobs = jobService.searchAvailableJobs(keyword);
        Map<String, Integer> acceptedCountsByJobId = jobs.stream()
                .collect(Collectors.toMap(Job::getId,
                        job -> recruitmentPolicyService.countAcceptedApplications(job.getId()),
                        (left, right) -> left));
        Map<String, Integer> remainingSlotsByJobId = jobs.stream()
                .collect(Collectors.toMap(Job::getId,
                        job -> recruitmentPolicyService.remainingAssistantSlots(job.getId()),
                        (left, right) -> left));
        Map<String, Boolean> fullJobsById = jobs.stream()
                .collect(Collectors.toMap(Job::getId,
                        job -> recruitmentPolicyService.isJobFull(job.getId()),
                        (left, right) -> left));

        request.setAttribute("jobs", jobs);
        request.setAttribute("searchKeyword", keyword == null ? "" : keyword.trim());
        request.setAttribute("profile", applicantService.findByUserId(currentUserId).orElse(null));
        request.setAttribute("hasUploadedCv", cvService.hasUploadedCv(currentUserId));
        request.setAttribute("activeApplicationCount", recruitmentPolicyService.countActiveApplications(currentUserId));
        request.setAttribute("effectiveApplicationLimit",
                recruitmentPolicyService.resolveApplicantApplicationLimit(currentUserId));
        request.setAttribute("hasReachedApplicationLimit",
                recruitmentPolicyService.hasReachedApplicationLimit(currentUserId));
        request.setAttribute("acceptedCountsByJobId", acceptedCountsByJobId);
        request.setAttribute("remainingSlotsByJobId", remainingSlotsByJobId);
        request.setAttribute("fullJobsById", fullJobsById);
        forward(request, response, "applicant/job_list.jsp");
    }
}
