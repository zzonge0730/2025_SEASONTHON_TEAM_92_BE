package com.tenantcollective.rentnegotiation.model;

public class NegotiationTip {
    private String category; // e.g., "RENT_RATE", "FACILITIES"
    private String message;
    private int priority; // 1 (high) to 3 (low)

    public NegotiationTip(String category, String message, int priority) {
        this.category = category;
        this.message = message;
        this.priority = priority;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
