package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.service.RecruitmentPolicyService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/organiser/jobs")
public class OrganiserJobsServlet extends BaseServlet {
    private final JobService jobService = new JobService();
    private final RecruitmentPolicyService recruitmentPolicyService = new RecruitmentPolicyService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        var jobs = jobService.getJobsByOrganiser(user.getId());
        Map<String, Integer> acceptedCountsByJobId = jobs.stream()
                .collect(Collectors.toMap(
                        com.bupt.tarecruit.model.Job::getId,
                        job -> recruitmentPolicyService.countAcceptedApplications(job.getId()),
                        (left, right) -> left));
        Map<String, Integer> remainingSlotsByJobId = jobs.stream()
                .collect(Collectors.toMap(
                        com.bupt.tarecruit.model.Job::getId,
                        job -> recruitmentPolicyService.remainingAssistantSlots(job.getId()),
                        (left, right) -> left));
        Map<String, Boolean> fullJobsById = jobs.stream()
                .collect(Collectors.toMap(
                        com.bupt.tarecruit.model.Job::getId,
                        job -> recruitmentPolicyService.isJobFull(job.getId()),
                        (left, right) -> left));
        request.setAttribute("jobs", jobs);
        request.setAttribute("acceptedCountsByJobId", acceptedCountsByJobId);
        request.setAttribute("remainingSlotsByJobId", remainingSlotsByJobId);
        request.setAttribute("fullJobsById", fullJobsById);
        forward(request, response, "organiser/job_list.jsp");
    }
}
