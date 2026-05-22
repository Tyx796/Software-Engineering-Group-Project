package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/settings")
public class AdminSettingsServlet extends BaseServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute(
                "defaultApplicantApplicationLimit",
                adminService.getGlobalDefaultApplicantApplicationLimit());
        forward(request, response, "admin/settings.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String value = request.getParameter("defaultApplicantApplicationLimit");
        try {
            int limit = Integer.parseInt(value);
            adminService.updateGlobalDefaultApplicantApplicationLimit(limit);
            setFlash(request, "Default applicant application limit updated.");
            redirect(request, response, "/admin/settings");
        } catch (NumberFormatException exception) {
            setError(request, "Default applicant application limit must be a whole number.");
            doGet(request, response);
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            doGet(request, response);
        }
    }
}
