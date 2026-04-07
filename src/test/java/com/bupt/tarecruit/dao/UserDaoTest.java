package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.dao.impl.UserDaoImpl;
import com.bupt.tarecruit.model.Role;
import com.bupt.tarecruit.model.User;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDaoTest {
    @Test
    void saveAndFindById() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserDao dao = new UserDaoImpl(file);

        User user = User.create("Alice", "alice@example.com", "hash", Role.APPLICANT);
        dao.save(user);

        assertTrue(dao.findById(user.getId()).isPresent());
        assertEquals("Alice", dao.findById(user.getId()).get().getUsername());
    }

    @Test
    void findByEmailIgnoresCase() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserDao dao = new UserDaoImpl(file);

        User user = User.create("Alice", "alice@example.com", "hash", Role.APPLICANT);
        dao.save(user);

        assertTrue(dao.findByEmail("Alice@Example.com").isPresent());
    }

    @Test
    void findAllReturnsAllSavedUsers() throws Exception {
        Path file = Files.createTempFile("users", ".json");
        UserDao dao = new UserDaoImpl(file);

        dao.save(User.create("Alice", "alice@example.com", "hash", Role.APPLICANT));
        dao.save(User.create("Bob", "bob@example.com", "hash", Role.ORGANISER));

        assertEquals(2, dao.findAll().size());
    }
}
