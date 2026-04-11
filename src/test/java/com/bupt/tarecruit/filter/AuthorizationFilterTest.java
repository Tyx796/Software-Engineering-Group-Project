package com.bupt.tarecruit.filter;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorizationFilterTest {
    @Test
    void adminRoutesRequireAdminRole() {
        User admin = User.create("admin", "admin@example.com", "hash", Role.ADMIN);
        User applicant = User.create("applicant", "applicant@example.com", "hash", Role.APPLICANT);

        assertTrue(AuthorizationFilter.isAuthorized(admin, "/app/admin/home"));
        assertFalse(AuthorizationFilter.isAuthorized(applicant, "/app/admin/home"));
    }

    @Test
    void existingApplicantAndOrganiserRoutesStillRequireMatchingRole() {
        User organiser = User.create("organiser", "organiser@example.com", "hash", Role.ORGANISER);
        User applicant = User.create("applicant", "applicant@example.com", "hash", Role.APPLICANT);

        assertTrue(AuthorizationFilter.isAuthorized(applicant, "/app/applicant/jobs"));
        assertFalse(AuthorizationFilter.isAuthorized(organiser, "/app/applicant/jobs"));
        assertTrue(AuthorizationFilter.isAuthorized(organiser, "/app/organiser/jobs"));
        assertFalse(AuthorizationFilter.isAuthorized(applicant, "/app/organiser/jobs"));
    }
}
