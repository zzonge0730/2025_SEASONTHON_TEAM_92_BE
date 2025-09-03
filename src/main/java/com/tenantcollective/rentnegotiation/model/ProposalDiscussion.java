package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class ProposalDiscussion {
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Proposal ID is required")
    private String proposalId;
    
    @NotBlank(message = "Author ID is required")
    private String authorId;
    
    @NotBlank(message = "Author role is required")
    @Pattern(regexp = "^(tenant|landlord)$", message = "Author role must be tenant or landlord")
    private String authorRole;
    
    @NotBlank(message = "Content is required")
    @Size(max = 1000, message = "Content must be less than 1000 characters")
    private String content;
    
    private Boolean isReply = false;
    private String parentId;

    // Constructors
    public ProposalDiscussion() {
        this.timestamp = LocalDateTime.now();
    }

    public ProposalDiscussion(String proposalId, String authorId, String authorRole, String content) {
        this();
        this.proposalId = proposalId;
        this.authorId = authorId;
        this.authorRole = authorRole;
        this.content = content;
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

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getIsReply() {
        return isReply;
    }

    public void setIsReply(Boolean isReply) {
        this.isReply = isReply;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
