package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class Vote {
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Proposal ID is required")
    private String proposalId;
    
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Vote is required")
    private String vote;

    // Constructors
    public Vote() {
        this.timestamp = LocalDateTime.now();
    }

    public Vote(String proposalId, String userId, String vote) {
        this();
        this.proposalId = proposalId;
        this.userId = userId;
        this.vote = vote;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }
}
