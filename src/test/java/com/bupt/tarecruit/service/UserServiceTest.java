package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.impl.UserDaoImpl;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.PasswordUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void registerStoresVersionedPasswordHash() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserService service = new UserService(file);

        User user = service.register("Alice", "alice@example.com", "secret1", Role.APPLICANT);

        assertTrue(user.getPasswordHash().startsWith("v2$"));
        assertFalse(PasswordUtil.needsRehash(user.getPasswordHash()));
    }

    @Test
    void loginMigratesLegacyHashToVersionedHash() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserService service = new UserService(file);

        User user = new User();
        user.setId("U-legacy");
        user.setUsername("Legacy");
        user.setEmail("legacy@example.com");
        user.setPasswordHash("ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f");
        user.setRole(Role.APPLICANT);
        new UserDaoImpl(file).save(user);

        User loggedIn = service.login("legacy@example.com", "password123");

        assertEquals("legacy@example.com", loggedIn.getEmail());
        String storedHash = new UserDaoImpl(file)
                .findById("U-legacy")
                .orElseThrow()
                .getPasswordHash();
        assertTrue(storedHash.startsWith("v2$"));
        assertFalse(PasswordUtil.needsRehash(storedHash));
    }
}
