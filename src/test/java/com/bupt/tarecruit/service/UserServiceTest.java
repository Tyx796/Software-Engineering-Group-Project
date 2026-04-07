package com.bupt.tarecruit.service;

import com.bupt.tarecruit.model.Role;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    @Test
    void registerAndLoginRoundTrip() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserService service = new UserService(file);

        service.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);

        assertEquals("alice@example.com", service.login("alice@example.com", "secret1").getEmail());
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserService service = new UserService(file);
        service.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);

        assertThrows(IllegalArgumentException.class,
                () -> service.register("Bob", "alice@example.com", "secret2", Role.ORGANISER));
    }

    @Test
    void invalidEmailIsRejected() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserService service = new UserService(file);

        assertThrows(IllegalArgumentException.class,
                () -> service.register("Alice", "alice-at-example", "secret1", Role.APPLICANT));
    }
}
