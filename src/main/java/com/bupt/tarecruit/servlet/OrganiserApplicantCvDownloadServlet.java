package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.CV;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.ApplicationService;
import com.bupt.tarecruit.service.CvService;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet("/organiser/applications/cv")
public class OrganiserApplicantCvDownloadServlet extends BaseServlet {
    private final ApplicationService applicationService = new ApplicationService();
    private final CvService cvService = new CvService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String applicationId = request.getParameter("applicationId");
        if (applicationId == null || applicationId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application ID is required.");
            return;
        }

        User organiser = SessionUtil.currentUser(request);
        try {
            Application application = applicationService.getApplicationDetailsForOrganiser(organiser.getId(), applicationId);
            CV cv = cvService.findAvailableCvByUserId(application.getApplicantUserId())
                    .orElseThrow(() -> new IllegalArgumentException("CV file not found."));
            Path filePath = FileStorageUtil.cvDirectory()
                    .resolve(application.getApplicantUserId())
                    .resolve(cv.getFileName());

            response.setContentType(contentTypeFor(cv.getFileType()));
            response.setContentLengthLong(Files.size(filePath));
            if ("pdf".equals(cv.getFileType())) {
                response.setHeader("Content-Disposition", "inline; filename=\"" + cv.getFileName() + "\"");
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + cv.getFileName() + "\"");
            }

            try (InputStream in = Files.newInputStream(filePath);
                 OutputStream out = response.getOutputStream()) {
                in.transferTo(out);
            }
        } catch (IllegalArgumentException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, exception.getMessage());
        }
    }

    private String contentTypeFor(final String fileType) {
        return switch (fileType) {
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }
}
