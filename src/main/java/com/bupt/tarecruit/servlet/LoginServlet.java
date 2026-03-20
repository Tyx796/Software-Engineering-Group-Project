package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.UserService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response, "login.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            User user = userService.login(request.getParameter("email"), request.getParameter("password"));
            SessionUtil.login(request, user);
            if (user.getRole() == Role.APPLICANT) {
                redirect(request, response, "/applicant/jobs");
                return;
            }
            if (user.getRole() == Role.ORGANISER) {
                redirect(request, response, "/organiser/jobs");
                return;
            }
            redirect(request, response, "/login");
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            request.setAttribute("formEmail", request.getParameter("email"));
            forward(request, response, "login.jsp");
        }
    }
}
