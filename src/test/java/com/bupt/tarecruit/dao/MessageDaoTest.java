package com.bupt.tarecruit.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.dao.impl.MessageDaoImpl;
import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.model.MessageType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class MessageDaoTest {
    @Test
    void saveAndFindByRecipientIdPersistMessageFields() throws Exception {
        Path file = Files.createTempFile("messages", ".json");
        MessageDao dao = new MessageDaoImpl(file);

        Message message = Message.create(
                "sender-1",
                "recipient-1",
                "Application Withdrawn",
                "A user withdrew an application.",
                MessageType.APPLICATION_WITHDRAWN);
        message.setRelatedApplicationId("APP-1");
        message.setRelatedJobId("JOB-1");
        dao.save(message);

        List<Message> messages = dao.findByRecipientId("recipient-1");
        assertEquals(1, messages.size());
        assertEquals("sender-1", messages.get(0).getSenderUserId());
        assertEquals("APP-1", messages.get(0).getRelatedApplicationId());
        assertEquals("JOB-1", messages.get(0).getRelatedJobId());
    }

    @Test
    void saveUpdatesExistingMessageReadTimestamp() throws Exception {
        Path file = Files.createTempFile("messages", ".json");
        MessageDao dao = new MessageDaoImpl(file);

        Message message = Message.create(
                "sender-1",
                "recipient-1",
                "Job Cancelled",
                "A job was cancelled.",
                MessageType.JOB_CANCELLED);
        dao.save(message);

        Instant readAt = Instant.now();
        message.setReadAt(readAt);
        dao.save(message);

        Message stored = dao.findById(message.getId()).orElseThrow();
        assertTrue(stored.isRead());
        assertEquals(readAt, stored.getReadAt());
    }
}
