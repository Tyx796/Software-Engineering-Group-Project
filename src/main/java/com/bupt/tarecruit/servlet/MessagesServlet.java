package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Job;
import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.service.JobService;
import com.bupt.tarecruit.service.MessageService;
import com.bupt.tarecruit.service.UserService;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = {"/applicant/messages", "/organiser/messages"})
public class MessagesServlet extends BaseServlet {
    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        User currentUser = SessionUtil.currentUser(request);
        var messages = messageService.getMessagesForRecipient(currentUser.getId());
        Map<String, User> usersById = userService.getAllUsers().stream()
                .collect(Collectors.toMap(User::getId, Function.identity(), (left, right) -> left));
        Map<String, Job> jobsById = jobService.getAllJobs().stream()
                .collect(Collectors.toMap(Job::getId, Function.identity(), (left, right) -> left));
        request.setAttribute("messages", messages);
        request.setAttribute("usersById", usersById);
        request.setAttribute("jobsById", jobsById);
        forward(request, response, "common/messages.jsp");
    }
}
