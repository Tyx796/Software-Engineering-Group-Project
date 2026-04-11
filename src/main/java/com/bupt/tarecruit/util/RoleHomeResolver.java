package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;

public final class RoleHomeResolver {
    private RoleHomeResolver() {
    }

    public static String landingPathFor(final User user) {
        if (user == null) {
            return "/login";
        }
        return landingPathFor(user.getRole());
    }

    public static String landingPathFor(final Role role) {
        if (role == null) {
            return "/login";
        }
        return switch (role) {
            case APPLICANT -> "/applicant/jobs";
            case ORGANISER -> "/organiser/jobs";
            case ADMIN -> "/admin/home";
        };
    }
}
