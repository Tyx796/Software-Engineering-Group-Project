package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> findAll();
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    void save(User user);
    void saveAll(List<User> users);
}
