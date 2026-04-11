package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import jakarta.servlet.http.HttpServletRequest;

public final class RoleChecker {
    private RoleChecker() {
    }

    public static boolean hasRole(final User user, final Role role) {
        return user != null && user.getRole() == role;
    }

    public static boolean isApplicant(final User user) {
        return hasRole(user, Role.APPLICANT);
    }

    public static boolean isOrganiser(final User user) {
        return hasRole(user, Role.ORGANISER);
    }

    public static boolean isAdmin(final User user) {
        return hasRole(user, Role.ADMIN);
    }

    public static boolean checkRole(final HttpServletRequest request, final Role expectedRole) {
        User user = SessionUtil.currentUser(request);
        return hasRole(user, expectedRole);
    }
}
