package com.tenantcollective.rentnegotiation.model;

import java.util.List;

public class AdvancedReport {

    private User userProfile;
    private MarketData marketData;
    private DiagnosisStats diagnosisStats;
    private List<NegotiationTip> negotiationStrategies;
    private DataReliability dataReliability;
    private List<String> keyFindings;
    private List<String> recommendations;

    // Getters and Setters
    public User getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(User userProfile) {
        this.userProfile = userProfile;
    }

    public MarketData getMarketData() {
        return marketData;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }

    public DiagnosisStats getDiagnosisStats() {
        return diagnosisStats;
    }

    public void setDiagnosisStats(DiagnosisStats diagnosisStats) {
        this.diagnosisStats = diagnosisStats;
    }

    public List<NegotiationTip> getNegotiationStrategies() {
        return negotiationStrategies;
    }

    public void setNegotiationStrategies(List<NegotiationTip> negotiationStrategies) {
        this.negotiationStrategies = negotiationStrategies;
    }

    public DataReliability getDataReliability() {
        return dataReliability;
    }

    public void setDataReliability(DataReliability dataReliability) {
        this.dataReliability = dataReliability;
    }

    public List<String> getKeyFindings() {
        return keyFindings;
    }

    public void setKeyFindings(List<String> keyFindings) {
        this.keyFindings = keyFindings;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
}
