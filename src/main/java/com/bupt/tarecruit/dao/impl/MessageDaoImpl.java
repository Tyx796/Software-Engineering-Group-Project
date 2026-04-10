package com.bupt.tarecruit.dao.impl;

import com.bupt.tarecruit.dao.MessageDao;
import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.util.FileStorageUtil;
import com.google.gson.reflect.TypeToken;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDaoImpl implements MessageDao {
    private final Path messagesFile;

    public MessageDaoImpl() {
        this(FileStorageUtil.dataDirectory().resolve("messages.json"));
    }

    public MessageDaoImpl(final Path messagesFile) {
        this.messagesFile = messagesFile;
    }

    @Override
    public List<Message> findAll() {
        return FileStorageUtil.readList(messagesFile, new TypeToken<>() { });
    }

    @Override
    public Optional<Message> findById(final String id) {
        return findAll().stream().filter(message -> message.getId().equals(id)).findFirst();
    }

    @Override
    public List<Message> findByRecipientId(final String recipientUserId) {
        return findAll().stream()
                .filter(message -> recipientUserId.equals(message.getRecipientUserId()))
                .toList();
    }

    @Override
    public void save(final Message message) {
        List<Message> messages = new ArrayList<>(findAll());
        messages.removeIf(existing -> existing.getId().equals(message.getId()));
        messages.add(message);
        saveAll(messages);
    }

    @Override
    public void saveAll(final List<Message> messages) {
        FileStorageUtil.writeList(messagesFile, messages);
    }
}
