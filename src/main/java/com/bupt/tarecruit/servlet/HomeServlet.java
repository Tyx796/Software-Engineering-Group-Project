package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HomeServlet extends BaseServlet {
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        User user = SessionUtil.currentUser(request);
        if (user == null) {
            redirect(request, response, "/login");
            return;
        }
        if (user.getRole() == Role.APPLICANT) {
            redirect(request, response, "/applicant/jobs");
            return;
        }
        if (user.getRole() == Role.ORGANISER) {
            redirect(request, response, "/organiser/jobs");
            return;
        }
        redirect(request, response, "/login");
    }
}
