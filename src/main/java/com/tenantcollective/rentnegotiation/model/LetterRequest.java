package com.tenantcollective.rentnegotiation.model;

import jakarta.validation.constraints.*;

public class LetterRequest {
    @NotBlank(message = "Group ID is required")
    private String groupId;
    
    @NotNull(message = "Cap percentage is required")
    @Min(value = 0, message = "Cap percentage must be non-negative")
    @Max(value = 100, message = "Cap percentage must not exceed 100")
    private Integer capPct;
    
    @NotNull(message = "Term months is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer termMonths;
    
    @NotNull(message = "Notice days is required")
    @Min(value = 1, message = "Notice must be at least 1 day")
    private Integer noticeDays;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Contact email must be valid")
    private String contactEmail;
    
    @NotBlank(message = "Contact phone is required")
    private String contactPhone;
    
    private java.util.List<String> selectedPainPoints;
    private java.util.List<String> selectedDiscussions;
    private String customContent;

    public LetterRequest() {}

    public LetterRequest(String groupId, Integer capPct, Integer termMonths, Integer noticeDays,
                        String contactEmail, String contactPhone) {
        this.groupId = groupId;
        this.capPct = capPct;
        this.termMonths = termMonths;
        this.noticeDays = noticeDays;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Integer getCapPct() {
        return capPct;
    }

    public void setCapPct(Integer capPct) {
        this.capPct = capPct;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public Integer getNoticeDays() {
        return noticeDays;
    }

    public void setNoticeDays(Integer noticeDays) {
        this.noticeDays = noticeDays;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public java.util.List<String> getSelectedPainPoints() {
        return selectedPainPoints;
    }

    public void setSelectedPainPoints(java.util.List<String> selectedPainPoints) {
        this.selectedPainPoints = selectedPainPoints;
    }

    public java.util.List<String> getSelectedDiscussions() {
        return selectedDiscussions;
    }

    public void setSelectedDiscussions(java.util.List<String> selectedDiscussions) {
        this.selectedDiscussions = selectedDiscussions;
    }

    public String getCustomContent() {
        return customContent;
    }

    public void setCustomContent(String customContent) {
        this.customContent = customContent;
    }
}