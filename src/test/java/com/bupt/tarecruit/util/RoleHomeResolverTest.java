package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleHomeResolverTest {
    @Test
    void landingPathDefaultsToLoginForNullUser() {
        assertEquals("/login", RoleHomeResolver.landingPathFor((User) null));
    }

    @Test
    void landingPathResolvesApplicantHome() {
        assertEquals("/applicant/jobs", RoleHomeResolver.landingPathFor(Role.APPLICANT));
    }

    @Test
    void landingPathResolvesOrganiserHome() {
        assertEquals("/organiser/jobs", RoleHomeResolver.landingPathFor(Role.ORGANISER));
    }

    @Test
    void landingPathResolvesAdminHome() {
        assertEquals("/admin/home", RoleHomeResolver.landingPathFor(Role.ADMIN));
    }
}
