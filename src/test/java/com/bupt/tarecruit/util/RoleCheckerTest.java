package com.bupt.tarecruit.util;

import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleCheckerTest {
    @Test
    void hasRoleReturnsTrueForMatchingRole() {
        User user = User.create("test", "test@test.com", "hash", Role.APPLICANT);
        assertTrue(RoleChecker.hasRole(user, Role.APPLICANT));
    }

    @Test
    void hasRoleReturnsFalseForMismatchedRole() {
        User user = User.create("test", "test@test.com", "hash", Role.APPLICANT);
        assertFalse(RoleChecker.hasRole(user, Role.ORGANISER));
    }

    @Test
    void hasRoleReturnsFalseForNullUser() {
        assertFalse(RoleChecker.hasRole(null, Role.APPLICANT));
    }

    @Test
    void isApplicantReturnsTrueForApplicant() {
        User user = User.create("test", "test@test.com", "hash", Role.APPLICANT);
        assertTrue(RoleChecker.isApplicant(user));
        assertFalse(RoleChecker.isOrganiser(user));
        assertFalse(RoleChecker.isAdmin(user));
    }

    @Test
    void isOrganiserReturnsTrueForOrganiser() {
        User user = User.create("test", "test@test.com", "hash", Role.ORGANISER);
        assertTrue(RoleChecker.isOrganiser(user));
        assertFalse(RoleChecker.isApplicant(user));
    }

    @Test
    void isAdminReturnsTrueForAdmin() {
        User user = User.create("test", "test@test.com", "hash", Role.ADMIN);
        assertTrue(RoleChecker.isAdmin(user));
        assertFalse(RoleChecker.isApplicant(user));
    }
}
