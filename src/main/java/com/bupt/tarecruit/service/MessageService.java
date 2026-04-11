package com.bupt.tarecruit.service;

import com.bupt.tarecruit.dao.MessageDao;
import com.bupt.tarecruit.dao.impl.MessageDaoImpl;
import com.bupt.tarecruit.model.Message;
import com.bupt.tarecruit.model.MessageType;
import com.bupt.tarecruit.util.DataValidator;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MessageService {
    private final MessageDao messageDao;

    public MessageService() {
        this(new MessageDaoImpl());
    }

    public MessageService(final Path messagesFile) {
        this(new MessageDaoImpl(messagesFile));
    }

    public MessageService(final MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public Message sendMessage(
            final String senderUserId,
            final String recipientUserId,
            final String subject,
            final String content,
            final MessageType type,
            final String relatedApplicationId,
            final String relatedJobId) {
        DataValidator.validateRequired(senderUserId, "Sender user ID");
        DataValidator.validateRequired(recipientUserId, "Recipient user ID");
        DataValidator.validateRequired(subject, "Message subject");
        DataValidator.validateRequired(content, "Message content");
        if (type == null) {
            throw new IllegalArgumentException("Message type is required.");
        }

        Message message = Message.create(senderUserId, recipientUserId, subject.trim(), content.trim(), type);
        message.setRelatedApplicationId(blankToNull(relatedApplicationId));
        message.setRelatedJobId(blankToNull(relatedJobId));
        messageDao.save(message);
        return message;
    }

    public List<Message> getMessagesForRecipient(final String recipientUserId) {
        DataValidator.validateRequired(recipientUserId, "Recipient user ID");
        return messageDao.findByRecipientId(recipientUserId).stream()
                .sorted(Comparator.comparing(
                        Message::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    public Optional<Message> getMessageDetails(final String messageId) {
        DataValidator.validateRequired(messageId, "Message ID");
        return messageDao.findById(messageId);
    }

    public Message markAsRead(final String recipientUserId, final String messageId) {
        DataValidator.validateRequired(recipientUserId, "Recipient user ID");
        DataValidator.validateRequired(messageId, "Message ID");

        Message message = messageDao.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found."));
        if (!recipientUserId.equals(message.getRecipientUserId())) {
            throw new IllegalArgumentException("You are not allowed to access this message.");
        }
        if (message.getReadAt() == null) {
            message.setReadAt(Instant.now());
            messageDao.save(message);
        }
        return message;
    }

    private String blankToNull(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
