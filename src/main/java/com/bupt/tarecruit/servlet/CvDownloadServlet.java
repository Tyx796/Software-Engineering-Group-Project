package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.CV;
import com.bupt.tarecruit.model.User;
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
import java.util.Optional;

@WebServlet("/applicant/cv/download")
public class CvDownloadServlet extends BaseServlet {
    private final CvService cvService = new CvService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User user = SessionUtil.currentUser(request);
        Optional<CV> cvOpt = cvService.findByUserId(user.getId());
        if (cvOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No CV uploaded.");
            return;
        }
        CV cv = cvOpt.get();
        Path filePath = FileStorageUtil.cvDirectory().resolve(user.getId()).resolve(cv.getFileName());
        if (!Files.exists(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "CV file not found on disk.");
            return;
        }

        String contentType = contentTypeFor(cv.getFileType());
        response.setContentType(contentType);
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
