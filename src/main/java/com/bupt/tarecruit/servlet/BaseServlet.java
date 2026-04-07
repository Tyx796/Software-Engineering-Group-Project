package com.bupt.tarecruit.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void forward(final HttpServletRequest request, final HttpServletResponse response, final String jsp)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/" + jsp).forward(request, response);
    }

    protected void redirect(final HttpServletRequest request, final HttpServletResponse response, final String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    protected void setFlash(final HttpServletRequest request, final String message) {
        request.getSession().setAttribute("flash", message);
    }

    protected void setError(final HttpServletRequest request, final String message) {
        request.setAttribute("error", message);
    }
}
