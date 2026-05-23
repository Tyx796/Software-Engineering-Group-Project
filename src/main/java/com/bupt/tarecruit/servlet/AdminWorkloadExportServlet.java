package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.AdminService;
import com.bupt.tarecruit.service.WorkloadReportCsvService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/admin/workloads/export")
public class AdminWorkloadExportServlet extends BaseServlet {
    private final AdminService adminService = new AdminService();
    private final WorkloadReportCsvService csvService = new WorkloadReportCsvService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        int workloadThreshold = AdminService.DEFAULT_WORKLOAD_THRESHOLD;
        String thresholdValue = request.getParameter("threshold");
        if (thresholdValue != null && !thresholdValue.isBlank()) {
            try {
                workloadThreshold = Integer.parseInt(thresholdValue.trim());
            } catch (NumberFormatException exception) {
                workloadThreshold = AdminService.DEFAULT_WORKLOAD_THRESHOLD;
            }
        }
        if (workloadThreshold <= 0) {
            workloadThreshold = AdminService.DEFAULT_WORKLOAD_THRESHOLD;
        }

        String csv = csvService.export(adminService.getApplicantWorkloadViews(workloadThreshold));
        response.setContentType("text/csv;charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment; filename=ta-workload-report.csv");
        response.getWriter().write(csv);
    }
}
