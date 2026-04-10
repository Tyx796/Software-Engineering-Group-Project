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
        request.setAttribute("formAction", request.getContextPath() + "/organiser/jobs/create");
        request.setAttribute("formHeading", "Create job posting");
        request.setAttribute("submitLabel", "Publish job");
        forward(request, response, "organiser/job_form.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        prepareForm(request);
        request.setAttribute("today", LocalDate.now());
        request.setAttribute("formAction", request.getContextPath() + "/organiser/jobs/create");
        request.setAttribute("formHeading", "Create job posting");
        request.setAttribute("submitLabel", "Publish job");
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
            forward(request, response, "organiser/job_form.jsp");
        } catch (java.time.format.DateTimeParseException exception) {
            setError(request, "Please choose a valid deadline.");
            forward(request, response, "organiser/job_form.jsp");
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            forward(request, response, "organiser/job_form.jsp");
        }
    }

    private void prepareForm(final HttpServletRequest request) {
        request.setAttribute("formTitle", request.getParameter("title"));
        request.setAttribute("formDepartment", request.getParameter("department"));
        request.setAttribute("formDescription", request.getParameter("description"));
        request.setAttribute("formRequirements", request.getParameter("requirements"));
        request.setAttribute("formHoursPerWeek", request.getParameter("hoursPerWeek"));
        request.setAttribute("formDeadline", request.getParameter("deadline"));
    }
}
