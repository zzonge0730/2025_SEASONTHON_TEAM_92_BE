package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class AnonymousReport {
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Building name is required")
    private String buildingName;
    
    @NotBlank(message = "Report content is required")
    @Size(max = 1000, message = "Report must be less than 1000 characters")
    private String report;
    
    private String neighborhood;
    private String city;
    private Boolean verified = false;

    // Constructors
    public AnonymousReport() {
        this.timestamp = LocalDateTime.now();
    }

    public AnonymousReport(String buildingName, String report, String neighborhood, String city) {
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
