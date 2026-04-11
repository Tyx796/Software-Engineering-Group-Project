package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.MessageService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/applicant/messages/read", "/organiser/messages/read"})
public class MessageReadServlet extends BaseServlet {
    private final MessageService messageService = new MessageService();

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String messageId = request.getParameter("messageId");
        if (messageId == null || messageId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Message ID is required.");
            return;
        }

        User currentUser = SessionUtil.currentUser(request);
        try {
            messageService.markAsRead(currentUser.getId(), messageId);
        } catch (IllegalArgumentException exception) {
            setFlash(request, exception.getMessage());
        }

        redirect(request, response, currentUser.getRole() == Role.APPLICANT
                ? "/applicant/messages"
                : "/organiser/messages");
    }
}
