package com.bupt.tarecruit.model;

import java.time.Instant;
import java.util.UUID;

public class Message {
    private String id;
    private String senderUserId;
    private String recipientUserId;
    private String subject;
    private String content;
    private String relatedApplicationId;
    private String relatedJobId;
    private Instant createdAt;
    private Instant readAt;
    private MessageType type;

    public static Message create(
            final String senderUserId,
            final String recipientUserId,
            final String subject,
            final String content,
            final MessageType type) {
        Message message = new Message();
        message.id = "MSG-" + UUID.randomUUID();
        message.senderUserId = senderUserId;
        message.recipientUserId = recipientUserId;
        message.subject = subject;
        message.content = content;
        message.type = type;
        message.createdAt = Instant.now();
        return message;
    }

    public boolean isRead() {
        return readAt != null;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(final String senderUserId) {
        this.senderUserId = senderUserId;
    }

    public String getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(final String recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getRelatedApplicationId() {
        return relatedApplicationId;
    }

    public void setRelatedApplicationId(final String relatedApplicationId) {
        this.relatedApplicationId = relatedApplicationId;
    }

    public String getRelatedJobId() {
        return relatedJobId;
    }

    public void setRelatedJobId(final String relatedJobId) {
        this.relatedJobId = relatedJobId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(final Instant readAt) {
        this.readAt = readAt;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(final MessageType type) {
        this.type = type;
    }
}
