package com.bupt.tarecruit.filter;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.RoleChecker;
import com.bupt.tarecruit.util.SessionUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/applicant/*", "/organiser/*", "/admin/*"})
public class AuthorizationFilter implements Filter {
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        User user = SessionUtil.currentUser(httpRequest);
        String uri = httpRequest.getRequestURI();
        if (!isAuthorized(user, uri)) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        chain.doFilter(request, response);
    }

    static boolean isAuthorized(final User user, final String uri) {
        if (uri.contains("/applicant/")) {
            return RoleChecker.hasRole(user, Role.APPLICANT);
        }
        if (uri.contains("/organiser/")) {
            return RoleChecker.hasRole(user, Role.ORGANISER);
        }
        if (uri.contains("/admin/")) {
            return RoleChecker.hasRole(user, Role.ADMIN);
        }
        return true;
    }
}
