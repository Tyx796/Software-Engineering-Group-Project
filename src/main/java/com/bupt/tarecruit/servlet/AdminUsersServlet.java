package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/users")
public class AdminUsersServlet extends BaseServlet {
    private final AdminService adminService = new AdminService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("applicantLimitViews", adminService.getApplicantLimitViews());
        forward(request, response, "admin/users.jsp");
    }
}
