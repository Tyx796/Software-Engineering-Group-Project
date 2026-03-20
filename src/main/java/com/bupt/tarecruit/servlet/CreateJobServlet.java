package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/organiser/jobs/create")
public class CreateJobServlet extends BaseServlet {
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response, "organiser/create_job.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        try {
            jobService.createJob(
                    user.getId(),
                    request.getParameter("title"),
                    request.getParameter("department"),
                    request.getParameter("description"),
                    request.getParameter("requirements"),
                    Integer.parseInt(request.getParameter("hoursPerWeek")),
                    LocalDate.parse(request.getParameter("deadline")));
            request.getSession().setAttribute("flash", "Job posted successfully.");
            redirect(request, response, "/organiser/jobs");
        } catch (Exception exception) {
            setError(request, exception.getMessage());
            forward(request, response, "organiser/create_job.jsp");
        }
    }
}
