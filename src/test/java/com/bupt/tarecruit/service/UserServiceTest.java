package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.model.Role;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class UserServiceTest {
    @Test
    void registerPersistsUserAndHashesPassword() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserService service = new UserService(file);

        var user = service.register("alice", "alice@example.com", "secret123", Role.APPLICANT);

        assertEquals("alice@example.com", user.getEmail());
        assertTrue(user.getPasswordHash().length() == 64);
        assertEquals(1, service.getAllUsers().size());
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserService service = new UserService(file);
        service.register("alice", "alice@example.com", "secret123", Role.APPLICANT);

        assertThrows(IllegalArgumentException.class,
                () -> service.register("alice2", "alice@example.com", "secret123", Role.APPLICANT));
    }
}
