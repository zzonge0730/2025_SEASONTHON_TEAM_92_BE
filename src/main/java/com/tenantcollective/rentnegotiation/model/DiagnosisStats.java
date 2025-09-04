package com.tenantcollective.rentnegotiation.model;

import java.util.Map;

public class DiagnosisStats {

    private int userParticipantCount; // Should be 1
    private int buildingParticipantCount;
    private int neighborhoodParticipantCount;

    private Map<String, Double> userScores; // 사용자의 점수
    private Map<String, Double> buildingAverageScores; // 같은 건물 이웃 평균 점수
    private Map<String, Double> neighborhoodAverageScores; // 같은 동네 이웃 평균 점수

    public DiagnosisStats(Map<String, Double> userScores, Map<String, Double> buildingAverageScores, Map<String, Double> neighborhoodAverageScores) {
        this.userScores = userScores;
        this.buildingAverageScores = buildingAverageScores;
        this.neighborhoodAverageScores = neighborhoodAverageScores;
    }

    // Getters and Setters
    public Map<String, Double> getUserScores() {
        return userScores;
    }

    public void setUserScores(Map<String, Double> userScores) {
        this.userScores = userScores;
    }

    public Map<String, Double> getBuildingAverageScores() {
        return buildingAverageScores;
    }

    public void setBuildingAverageScores(Map<String, Double> buildingAverageScores) {
        this.buildingAverageScores = buildingAverageScores;
    }

    public Map<String, Double> getNeighborhoodAverageScores() {
        return neighborhoodAverageScores;
    }

    public void setNeighborhoodAverageScores(Map<String, Double> neighborhoodAverageScores) {
        this.neighborhoodAverageScores = neighborhoodAverageScores;
    }

    public int getUserParticipantCount() {
        return userParticipantCount;
    }

    public void setUserParticipantCount(int userParticipantCount) {
        this.userParticipantCount = userParticipantCount;
    }

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
}
