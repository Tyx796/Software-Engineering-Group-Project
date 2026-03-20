package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.ApplicantProfile;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.ApplicantService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/applicant/profile")
public class ApplicantProfileServlet extends BaseServlet {
    private final ApplicantService applicantService = new ApplicantService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        applicantService.findByUserId(user.getId()).ifPresent(profile -> request.setAttribute("profile", profile));
        forward(request, response, "applicant/profile.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        try {
            ApplicantProfile profile = applicantService.createOrUpdateProfile(
                    user.getId(),
                    request.getParameter("fullName"),
                    request.getParameter("phone"),
                    request.getParameter("studentId"),
                    request.getParameter("programme"),
                    request.getParameter("bio"));
            request.getSession().setAttribute("flash", "Profile saved successfully.");
            request.setAttribute("profile", profile);
            redirect(request, response, "/applicant/profile");
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            ApplicantProfile profile = new ApplicantProfile();
            profile.setFullName(request.getParameter("fullName"));
            profile.setPhone(request.getParameter("phone"));
            profile.setStudentId(request.getParameter("studentId"));
            profile.setProgramme(request.getParameter("programme"));
            profile.setBio(request.getParameter("bio"));
            request.setAttribute("profile", profile);
            forward(request, response, "applicant/profile.jsp");
        }
    }
}
