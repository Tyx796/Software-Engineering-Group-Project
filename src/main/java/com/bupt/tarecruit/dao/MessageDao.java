package com.bupt.tarecruit.dao;

import com.bupt.tarecruit.model.Message;
import java.util.List;
import java.util.Optional;

public interface MessageDao {
    List<Message> findAll();
    Optional<Message> findById(String id);
    List<Message> findByRecipientId(String recipientUserId);
    void save(Message message);
    void saveAll(List<Message> messages);
}
