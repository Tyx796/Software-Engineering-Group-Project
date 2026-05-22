package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/home")
public class AdminHomeServlet extends BaseServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("dashboardSummary", adminService.getDashboardSummary());
        request.setAttribute("defaultApplicantApplicationLimit", adminService.getGlobalDefaultApplicantApplicationLimit());
        request.setAttribute("defaultWorkloadThreshold", AdminService.DEFAULT_WORKLOAD_THRESHOLD);
        request.setAttribute("applicantsUsingOverrideCount", adminService.countApplicantsUsingOverride());
        request.setAttribute("applicantsOverLimitCount", adminService.countApplicantsOverEffectiveLimit());
        request.setAttribute(
                "overloadedApplicantViews",
                adminService.getApplicantWorkloadViews(AdminService.DEFAULT_WORKLOAD_THRESHOLD).stream()
                        .filter(view -> view.isOverloaded())
                        .limit(5)
                        .toList());
        request.setAttribute(
                "jobRiskViews",
                adminService.getJobSupervisionViews().stream()
                        .filter(view -> view.isAcceptedOverQuota() || view.hasUnexpectedPendingOrReviewingWhenFull())
                        .limit(5)
                        .toList());
        forward(request, response, "admin/home.jsp");
    }
}
