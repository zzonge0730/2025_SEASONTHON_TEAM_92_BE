package com.tenantcollective.rentnegotiation.model;

public class NegotiationCard {
    private String issueId;
    private String issueName;
    private IssueCategory category;
    private String description;
    private String negotiationStrategy;
    private int priority;
    private double scoreDifference;
    private String legalBasis;
    private String suggestedAction;
    
    // Constructors
    public NegotiationCard() {}
    
    public NegotiationCard(String issueId, String issueName, IssueCategory category, 
                          String description, String negotiationStrategy, int priority,
                          double scoreDifference, String legalBasis, String suggestedAction) {
        this.issueId = issueId;
        this.issueName = issueName;
        this.category = category;
        this.description = description;
        this.negotiationStrategy = negotiationStrategy;
        this.priority = priority;
        this.scoreDifference = scoreDifference;
        this.legalBasis = legalBasis;
        this.suggestedAction = suggestedAction;
    }
    
    // Getters and Setters
    public String getIssueId() {
        return issueId;
    }
    
    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
    
    public String getIssueName() {
        return issueName;
    }
    
    public void setIssueName(String issueName) {
        this.issueName = issueName;
    }
    
    public IssueCategory getCategory() {
        return category;
    }
    
    public void setCategory(IssueCategory category) {
        this.category = category;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getNegotiationStrategy() {
        return negotiationStrategy;
    }
    
    public void setNegotiationStrategy(String negotiationStrategy) {
        this.negotiationStrategy = negotiationStrategy;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public double getScoreDifference() {
        return scoreDifference;
    }
    
    public void setScoreDifference(double scoreDifference) {
        this.scoreDifference = scoreDifference;
    }
    
    public String getLegalBasis() {
        return legalBasis;
    }
    
    public void setLegalBasis(String legalBasis) {
        this.legalBasis = legalBasis;
    }
    
    public String getSuggestedAction() {
        return suggestedAction;
    }
    
    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }
}