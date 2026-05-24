package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/users/limit")
public class AdminApplicantLimitServlet extends BaseServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String applicantUserId = request.getParameter("applicantUserId");
        String overrideValue = request.getParameter("applicationLimitOverride");
        try {
            if (overrideValue == null || overrideValue.isBlank()) {
                adminService.clearApplicantApplicationLimitOverride(applicantUserId);
                setFlash(request, "Applicant-specific application limit override cleared.");
            } else {
                int override = Integer.parseInt(overrideValue.trim());
                adminService.saveApplicantApplicationLimitOverride(applicantUserId, override);
                setFlash(request, "Applicant-specific application limit override updated.");
            }
            redirect(request, response, "/admin/users");
        } catch (NumberFormatException exception) {
            setError(request, "Applicant application limit override must be a whole number.");
            request.setAttribute("applicantLimitViews", adminService.getApplicantLimitViews());
            forward(request, response, "admin/users.jsp");
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            request.setAttribute("applicantLimitViews", adminService.getApplicantLimitViews());
            forward(request, response, "admin/users.jsp");
        }
    }
}
