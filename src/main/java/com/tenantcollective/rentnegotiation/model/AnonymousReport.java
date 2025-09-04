package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public class AnonymousReport {
    private String reportId;
    private String title;
    private String summary;
    private LocalDateTime createdAt;
    private DataReliability dataReliability;
    private List<NegotiationCard> negotiationCards;
    private String negotiationScenario;
    private double successRate;
    private int accessCount;
    private LocalDateTime lastAccessedAt;

    // Constructors
    public AnonymousReport() {}

    // Getters and Setters
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public DataReliability getDataReliability() {
        return dataReliability;
    }

    public void setDataReliability(DataReliability dataReliability) {
        this.dataReliability = dataReliability;
    }

    public List<NegotiationCard> getNegotiationCards() {
        return negotiationCards;
    }

    public void setNegotiationCards(List<NegotiationCard> negotiationCards) {
        this.negotiationCards = negotiationCards;
    }

    public String getNegotiationScenario() {
        return negotiationScenario;
    }

    public void setNegotiationScenario(String negotiationScenario) {
        this.negotiationScenario = negotiationScenario;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
}
