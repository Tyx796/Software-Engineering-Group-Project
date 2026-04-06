package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.UserDao;
import com.bupt.tarecruit.model.User;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private final Path usersFile;

    public UserDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("users.json"));
    }

    public UserDaoImpl(final Path usersFile) {
        this.usersFile = usersFile;
    }

    @Override
    public List<User> findAll() {
        return FileStorageUtil.readList(usersFile, new TypeToken<>() { });
    }

    @Override
    public Optional<User> findById(final String id) {
        return findAll().stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return findAll().stream().filter(user -> user.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    @Override
    public void save(final User user) {
        List<User> users = new ArrayList<>(findAll());
        users.removeIf(existing -> existing.getId().equals(user.getId()));
        users.add(user);
        saveAll(users);
    }

    @Override
    public void saveAll(final List<User> users) {
        FileStorageUtil.writeList(usersFile, users);
    }
}
