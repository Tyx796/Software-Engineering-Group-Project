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
        request.setAttribute("today", LocalDate.now());
        forward(request, response, "organiser/create_job.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        try {
            String hoursValue = request.getParameter("hoursPerWeek");
            String deadlineValue = request.getParameter("deadline");
            jobService.createJob(
                    user.getId(),
                    request.getParameter("title"),
                    request.getParameter("department"),
                    request.getParameter("description"),
                    request.getParameter("requirements"),
                    Integer.parseInt(hoursValue),
                    LocalDate.parse(deadlineValue));
            request.getSession().setAttribute("flash", "Job posted successfully.");
            redirect(request, response, "/organiser/jobs");
        } catch (NumberFormatException exception) {
            setError(request, "Hours per week must be a valid number.");
            request.setAttribute("today", LocalDate.now());
            forward(request, response, "organiser/create_job.jsp");
        } catch (java.time.format.DateTimeParseException exception) {
            setError(request, "Please choose a valid deadline.");
            request.setAttribute("today", LocalDate.now());
            forward(request, response, "organiser/create_job.jsp");
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            request.setAttribute("today", LocalDate.now());
            forward(request, response, "organiser/create_job.jsp");
        }
    }
}
