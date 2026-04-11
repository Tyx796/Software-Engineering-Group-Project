package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/organiser/jobs/edit")
public class EditJobServlet extends BaseServlet {
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String jobId = request.getParameter("id");
        if (jobId == null || jobId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required.");
            return;
        }

        User organiser = SessionUtil.currentUser(request);
        try {
            Job job = jobService.getOwnedJobForOrganiser(organiser.getId(), jobId);
            populateForm(request, job);
            request.setAttribute("jobId", job.getId());
            request.setAttribute("today", LocalDate.now());
            request.setAttribute("formAction", request.getContextPath() + "/organiser/jobs/edit");
            request.setAttribute("formHeading", "Edit job posting");
            request.setAttribute("submitLabel", "Save changes");
            forward(request, response, "organiser/job_form.jsp");
        } catch (IllegalArgumentException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String jobId = request.getParameter("jobId");
        if (jobId == null || jobId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Job ID is required.");
            return;
        }

        User organiser = SessionUtil.currentUser(request);
        prepareForm(request);
        request.setAttribute("jobId", jobId);
        request.setAttribute("today", LocalDate.now());
        request.setAttribute("formAction", request.getContextPath() + "/organiser/jobs/edit");
        request.setAttribute("formHeading", "Edit job posting");
        request.setAttribute("submitLabel", "Save changes");

        try {
            String hoursValue = request.getParameter("hoursPerWeek");
            String deadlineValue = request.getParameter("deadline");
            jobService.updateJobForOrganiser(
                    organiser.getId(),
                    jobId,
                    request.getParameter("title"),
                    request.getParameter("department"),
                    request.getParameter("description"),
                    request.getParameter("requirements"),
                    Integer.parseInt(hoursValue),
                    LocalDate.parse(deadlineValue));
            setFlash(request, "Job updated successfully.");
            redirect(request, response, "/organiser/jobs");
        } catch (NumberFormatException exception) {
            setError(request, "Hours per week must be a valid number.");
            forward(request, response, "organiser/job_form.jsp");
        } catch (java.time.format.DateTimeParseException exception) {
            setError(request, "Please choose a valid deadline.");
            forward(request, response, "organiser/job_form.jsp");
        } catch (IllegalArgumentException exception) {
            if ("The selected job does not exist.".equals(exception.getMessage())
                    || "You are not allowed to access this job.".equals(exception.getMessage())) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
                return;
            }
            setError(request, exception.getMessage());
            forward(request, response, "organiser/job_form.jsp");
        }
    }

    private void populateForm(final HttpServletRequest request, final Job job) {
        request.setAttribute("formTitle", job.getTitle());
        request.setAttribute("formDepartment", job.getDepartment());
        request.setAttribute("formDescription", job.getDescription());
        request.setAttribute("formRequirements", jobService.formatRequirements(job.getRequirements()));
        request.setAttribute("formHoursPerWeek", job.getHoursPerWeek());
        request.setAttribute("formDeadline", job.getDeadline());
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
