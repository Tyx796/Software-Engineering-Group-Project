package com.bupt.tarecruit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.model.MessageType;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class MessageServiceTest {
    @Test
    void sendMessageCreatesUnreadNotificationForRecipient() throws Exception {
        Path file = Files.createTempFile("messages", ".json");
        MessageService service = new MessageService(file);

        Message message = service.sendMessage(
                "sender-1",
                "recipient-1",
                "Application Withdrawn",
                "Applicant demo withdrew an accepted application.",
                MessageType.APPLICATION_WITHDRAWN,
                "APP-1",
                "JOB-1");

        assertEquals("recipient-1", message.getRecipientUserId());
        assertEquals(1, service.getMessagesForRecipient("recipient-1").size());
        assertTrue(service.getMessagesForRecipient("recipient-1").stream()
                .anyMatch(current -> current.getId().equals(message.getId()) && !current.isRead()));
    }

    @Test
    void getMessagesForRecipientReturnsNewestFirst() throws Exception {
        Path file = Files.createTempFile("messages", ".json");
        MessageService service = new MessageService(file);

        Message earlier = service.sendMessage(
                "sender-1",
                "recipient-1",
                "Earlier",
                "Earlier message.",
                MessageType.APPLICATION_WITHDRAWN,
                null,
                null);
        Thread.sleep(5L);
        Message later = service.sendMessage(
                "sender-2",
                "recipient-1",
                "Later",
                "Later message.",
                MessageType.JOB_CANCELLED,
                null,
                "JOB-1");

        assertEquals(later.getId(), service.getMessagesForRecipient("recipient-1").get(0).getId());
        assertEquals(earlier.getId(), service.getMessagesForRecipient("recipient-1").get(1).getId());
    }

    @Test
    void recipientCanMarkOwnMessageAsRead() throws Exception {
        Path file = Files.createTempFile("messages", ".json");
        MessageService service = new MessageService(file);

        Message message = service.sendMessage(
                "sender-1",
                "recipient-1",
                "Application Withdrawn",
                "Applicant demo withdrew an accepted application.",
                MessageType.APPLICATION_WITHDRAWN,
                "APP-1",
                "JOB-1");

        Message read = service.markAsRead("recipient-1", message.getId());

        assertNotNull(read.getReadAt());
        assertTrue(read.isRead());
    }

    @Test
    void otherUsersCannotMarkMessageAsRead() throws Exception {
        Path file = Files.createTempFile("messages", ".json");
        MessageService service = new MessageService(file);

        Message message = service.sendMessage(
                "sender-1",
                "recipient-1",
                "Job Cancelled",
                "A job was cancelled.",
                MessageType.JOB_CANCELLED,
                null,
                "JOB-1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.markAsRead("recipient-2", message.getId()));
        assertEquals("You are not allowed to access this message.", exception.getMessage());
    }
}
