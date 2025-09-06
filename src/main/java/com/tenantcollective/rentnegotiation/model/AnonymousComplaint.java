package com.tenantcollective.rentnegotiation.model;

import java.time.LocalDateTime;

public class AnonymousComplaint {
    private String id;
    private String timestamp;
    private String buildingName;
    private String report;
    private String neighborhood;
    private String city;
    private boolean verified;
    private String verifiedBy;
    private LocalDateTime verifiedAt;

    public AnonymousComplaint() {
        this.timestamp = LocalDateTime.now().toString();
        this.verified = false;
    }

    public AnonymousComplaint(String buildingName, String report, String neighborhood, String city) {
        this();
        this.buildingName = buildingName;
        this.report = report;
        this.neighborhood = neighborhood;
        this.city = city;
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

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}