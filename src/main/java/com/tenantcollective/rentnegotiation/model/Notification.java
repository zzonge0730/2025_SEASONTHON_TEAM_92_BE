package com.tenantcollective.rentnegotiation.model;

import java.time.LocalDateTime;

public class Notification {
    private String id;
    private String timestamp;
    private String userId;
    private String type; // 'proposal', 'vote', 'discussion', 'system', 'verification'
    private String title;
    private String message;
    private boolean isRead;
    private String relatedId; // proposalId, voteId, etc.
    private String actionUrl;
    private String priority; // 'low', 'medium', 'high'

    public Notification() {
        this.timestamp = LocalDateTime.now().toString();
        this.isRead = false;
        this.priority = "medium";
    }

    public Notification(String userId, String type, String title, String message) {
        this();
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}