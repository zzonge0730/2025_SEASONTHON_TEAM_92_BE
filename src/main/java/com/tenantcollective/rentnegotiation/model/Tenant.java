package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class Tenant {
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Building name is required")
    private String buildingName;
    
    @NotBlank(message = "Building type is required")
    @Pattern(regexp = "^(apartment|officetel|villa)$", message = "Building type must be apartment, officetel, or villa")
    private String buildingType;
    
    @NotBlank(message = "Contract type is required")
    @Pattern(regexp = "^(monthly|yearly)$", message = "Contract type must be monthly or yearly")
    private String contractType;
    
    @NotBlank(message = "Street address is required")
    private String streetAddress;
    
    @NotBlank(message = "Neighborhood is required")
    private String neighborhood;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotNull(message = "Current rent is required")
    @Positive(message = "Current rent must be positive")
    private Integer currentRentKrw;
    
    @NotNull(message = "Deposit is required")
    @Positive(message = "Deposit must be positive")
    private Integer depositKrw;
    
    @NotBlank(message = "Lease end date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "Lease end must be in YYYY-MM format")
    private String leaseEndYyyyMm;
    
    private Integer increaseNoticePctOptional;
    
    private String landlordEmailOptional;
    
    private String painPointsFreeText;
    
    @NotNull(message = "Consent is required")
    private Boolean consentYesNo;
    
    private String userId;

    // Constructors
    public Tenant() {
        this.timestamp = LocalDateTime.now();
    }

    public Tenant(String buildingName, String buildingType, String contractType, String streetAddress, String neighborhood, String city,
                  Integer currentRentKrw, Integer depositKrw, String leaseEndYyyyMm,
                  Integer increaseNoticePctOptional, String landlordEmailOptional,
                  String painPointsFreeText, Boolean consentYesNo) {
        this();
        this.buildingName = buildingName;
        this.buildingType = buildingType;
        this.contractType = contractType;
        this.streetAddress = streetAddress;
        this.neighborhood = neighborhood;
        this.city = city;
        this.currentRentKrw = currentRentKrw;
        this.depositKrw = depositKrw;
        this.leaseEndYyyyMm = leaseEndYyyyMm;
        this.increaseNoticePctOptional = increaseNoticePctOptional;
        this.landlordEmailOptional = landlordEmailOptional;
        this.painPointsFreeText = painPointsFreeText;
        this.consentYesNo = consentYesNo;
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

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
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

    public Integer getCurrentRentKrw() {
        return currentRentKrw;
    }

    public void setCurrentRentKrw(Integer currentRentKrw) {
        this.currentRentKrw = currentRentKrw;
    }

    public Integer getDepositKrw() {
        return depositKrw;
    }

    public void setDepositKrw(Integer depositKrw) {
        this.depositKrw = depositKrw;
    }

    public String getLeaseEndYyyyMm() {
        return leaseEndYyyyMm;
    }

    public void setLeaseEndYyyyMm(String leaseEndYyyyMm) {
        this.leaseEndYyyyMm = leaseEndYyyyMm;
    }

    public Integer getIncreaseNoticePctOptional() {
        return increaseNoticePctOptional;
    }

    public void setIncreaseNoticePctOptional(Integer increaseNoticePctOptional) {
        this.increaseNoticePctOptional = increaseNoticePctOptional;
    }

    public String getLandlordEmailOptional() {
        return landlordEmailOptional;
    }

    public void setLandlordEmailOptional(String landlordEmailOptional) {
        this.landlordEmailOptional = landlordEmailOptional;
    }

    public String getPainPointsFreeText() {
        return painPointsFreeText;
    }

    public void setPainPointsFreeText(String painPointsFreeText) {
        this.painPointsFreeText = painPointsFreeText;
    }

    public Boolean getConsentYesNo() {
        return consentYesNo;
    }

    public void setConsentYesNo(Boolean consentYesNo) {
        this.consentYesNo = consentYesNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}