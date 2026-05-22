package com.bupt.tarecruit.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts unexpected runtime errors into a generic error page while logging the
 * technical details server-side.
 */
public class ErrorHandlingFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandlingFilter.class.getName());
    private static final String SAFE_ERROR_MESSAGE = "An unexpected error occurred. Please try again later.";

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException exception) {
            logUnexpectedError(request, exception);
            if (response instanceof HttpServletResponse httpResponse && !httpResponse.isCommitted()) {
                request.setAttribute("error", SAFE_ERROR_MESSAGE);
                httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/error/error.jsp");
                dispatcher.forward(request, response);
                return;
            }
            throw exception;
        }
    }

    private void logUnexpectedError(final ServletRequest request, final Exception exception) {
        if (request instanceof HttpServletRequest httpRequest) {
            LOGGER.log(
                    Level.SEVERE,
                    "Unexpected error while handling " + httpRequest.getMethod() + " " + httpRequest.getRequestURI(),
                    exception);
            return;
        }
        LOGGER.log(Level.SEVERE, "Unexpected error while handling request.", exception);
    }
}
