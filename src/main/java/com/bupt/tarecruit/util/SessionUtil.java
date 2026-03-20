package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    private SessionUtil() {
    }

    public static void login(final HttpServletRequest request, final User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute("currentUser", user);
    }

    public static User currentUser(final HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }

    public static boolean hasRole(final HttpServletRequest request, final Role role) {
        User user = currentUser(request);
        return user != null && user.getRole() == role;
    }
}
