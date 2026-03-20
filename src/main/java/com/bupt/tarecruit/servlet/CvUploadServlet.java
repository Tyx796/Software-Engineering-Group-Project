package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.CvService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;

@WebServlet("/applicant/cv")
@MultipartConfig
public class CvUploadServlet extends BaseServlet {
    private final CvService cvService = new CvService();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        Part part = request.getPart("cv");
        try {
            cvService.upload(user.getId(), part.getSubmittedFileName(), part.getInputStream());
            request.getSession().setAttribute("flash", "CV uploaded successfully.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            request.getSession().setAttribute("flash", exception.getMessage());
        }
        redirect(request, response, "/applicant/profile");
    }
}
