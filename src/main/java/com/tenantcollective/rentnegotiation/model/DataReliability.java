package com.tenantcollective.rentnegotiation.model;

import java.util.Map;

public class DataReliability {
    private int buildingParticipantCount;
    private int neighborhoodParticipantCount;
    private double buildingReliabilityScore;
    private double neighborhoodReliabilityScore;
    private boolean isReportEligible;
    private String reliabilityMessage;
    private Map<String, Integer> categoryParticipantCounts;
    
    // Constructors
    public DataReliability() {}
    
    public DataReliability(int buildingParticipantCount, int neighborhoodParticipantCount, 
                          double buildingReliabilityScore, double neighborhoodReliabilityScore,
                          boolean isReportEligible, String reliabilityMessage) {
        this.buildingParticipantCount = buildingParticipantCount;
        this.neighborhoodParticipantCount = neighborhoodParticipantCount;
        this.buildingReliabilityScore = buildingReliabilityScore;
        this.neighborhoodReliabilityScore = neighborhoodReliabilityScore;
        this.isReportEligible = isReportEligible;
        this.reliabilityMessage = reliabilityMessage;
    }
    
    // Getters and Setters
    public int getBuildingParticipantCount() {
        return buildingParticipantCount;
    }
    
    public void setBuildingParticipantCount(int buildingParticipantCount) {
        this.buildingParticipantCount = buildingParticipantCount;
    }
    
    public int getNeighborhoodParticipantCount() {
        return neighborhoodParticipantCount;
    }
    
    public void setNeighborhoodParticipantCount(int neighborhoodParticipantCount) {
        this.neighborhoodParticipantCount = neighborhoodParticipantCount;
    }
    
    public double getBuildingReliabilityScore() {
        return buildingReliabilityScore;
    }
    
    public void setBuildingReliabilityScore(double buildingReliabilityScore) {
        this.buildingReliabilityScore = buildingReliabilityScore;
    }
    
    public double getNeighborhoodReliabilityScore() {
        return neighborhoodReliabilityScore;
    }
    
    public void setNeighborhoodReliabilityScore(double neighborhoodReliabilityScore) {
        this.neighborhoodReliabilityScore = neighborhoodReliabilityScore;
    }
    
    public boolean isReportEligible() {
        return isReportEligible;
    }
    
    public void setReportEligible(boolean reportEligible) {
        isReportEligible = reportEligible;
    }
    
    public String getReliabilityMessage() {
        return reliabilityMessage;
    }
    
    public void setReliabilityMessage(String reliabilityMessage) {
        this.reliabilityMessage = reliabilityMessage;
    }
    
    public Map<String, Integer> getCategoryParticipantCounts() {
        return categoryParticipantCounts;
    }
    
    public void setCategoryParticipantCounts(Map<String, Integer> categoryParticipantCounts) {
        this.categoryParticipantCounts = categoryParticipantCounts;
    }
}