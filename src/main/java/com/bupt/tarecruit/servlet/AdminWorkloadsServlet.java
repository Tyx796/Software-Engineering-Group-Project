package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/workloads")
public class AdminWorkloadsServlet extends BaseServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        int workloadThreshold = AdminService.DEFAULT_WORKLOAD_THRESHOLD;
        String thresholdValue = request.getParameter("threshold");
        if (thresholdValue != null && !thresholdValue.isBlank()) {
            try {
                workloadThreshold = Integer.parseInt(thresholdValue.trim());
            } catch (NumberFormatException exception) {
                setError(request, "Workload threshold must be a whole number.");
            }
        }
        try {
            request.setAttribute("workloadThreshold", workloadThreshold);
            request.setAttribute("workloadViews", adminService.getApplicantWorkloadViews(workloadThreshold));
        } catch (IllegalArgumentException exception) {
            request.setAttribute("workloadThreshold", AdminService.DEFAULT_WORKLOAD_THRESHOLD);
            request.setAttribute(
                    "workloadViews",
                    adminService.getApplicantWorkloadViews(AdminService.DEFAULT_WORKLOAD_THRESHOLD));
            setError(request, exception.getMessage());
        }
        forward(request, response, "admin/workloads.jsp");
    }
}
