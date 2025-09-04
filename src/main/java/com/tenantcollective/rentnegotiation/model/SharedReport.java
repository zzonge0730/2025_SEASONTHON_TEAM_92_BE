package com.tenantcollective.rentnegotiation.model;

import java.time.LocalDateTime;

public class SharedReport {
    private String reportId;
    private String userId;
    private String shareToken;
    private String reportUrl;
    private String title;
    private String summary;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private int accessCount;
    private String accessorInfo; // 임대인 정보 (선택적)
    
    // Constructors
    public SharedReport() {}
    
    public SharedReport(String reportId, String userId, String shareToken, String reportUrl, 
                       String title, String summary) {
        this.reportId = reportId;
        this.userId = userId;
        this.shareToken = shareToken;
        this.reportUrl = reportUrl;
        this.title = title;
        this.summary = summary;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.accessCount = 0;
    }
    
    // Getters and Setters
    public String getReportId() {
        return reportId;
    }
    
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getShareToken() {
        return shareToken;
    }
    
    public void setShareToken(String shareToken) {
        this.shareToken = shareToken;
    }
    
    public String getReportUrl() {
        return reportUrl;
    }
    
    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
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
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }
    
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
    
    public int getAccessCount() {
        return accessCount;
    }
    
    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }
    
    public String getAccessorInfo() {
        return accessorInfo;
    }
    
    public void setAccessorInfo(String accessorInfo) {
        this.accessorInfo = accessorInfo;
    }
}