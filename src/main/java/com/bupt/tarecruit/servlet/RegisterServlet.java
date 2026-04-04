package com.bupt.tarecruit.servlet;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        forward(request, response, "register.jsp");
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        try {
            if (confirmPassword == null || !confirmPassword.equals(password)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
            userService.register(
                    request.getParameter("username"),
                    request.getParameter("email"),
                    password,
                    Role.fromString(request.getParameter("role")));
            request.getSession().setAttribute("flash", "Registration successful. Please sign in.");
            redirect(request, response, "/login");
        } catch (IllegalArgumentException exception) {
            setError(request, exception.getMessage());
            request.setAttribute("formEmail", request.getParameter("email"));
            request.setAttribute("formUsername", request.getParameter("username"));
            request.setAttribute("formRole", request.getParameter("role"));
            forward(request, response, "register.jsp");
        }
    }
}
