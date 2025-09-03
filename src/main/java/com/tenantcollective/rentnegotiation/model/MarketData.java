package com.tenantcollective.rentnegotiation.model;

public class MarketData {
    private String neighborhood;
    private String buildingName;
    private Double avgDeposit;
    private Double avgMonthlyRent;
    private Double medianDeposit;
    private Double medianMonthlyRent;
    private Integer transactionCount;
    private String recentTransactionDate;

    public MarketData() {}

    public MarketData(String neighborhood, String buildingName, Double avgDeposit, 
                     Double avgMonthlyRent, Double medianDeposit, Double medianMonthlyRent,
                     Integer transactionCount, String recentTransactionDate) {
        this.neighborhood = neighborhood;
        this.buildingName = buildingName;
        this.avgDeposit = avgDeposit;
        this.avgMonthlyRent = avgMonthlyRent;
        this.medianDeposit = medianDeposit;
        this.medianMonthlyRent = medianMonthlyRent;
        this.transactionCount = transactionCount;
        this.recentTransactionDate = recentTransactionDate;
    }

    // Getters and Setters
    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Double getAvgDeposit() {
        return avgDeposit;
    }

    public void setAvgDeposit(Double avgDeposit) {
        this.avgDeposit = avgDeposit;
    }

    public Double getAvgMonthlyRent() {
        return avgMonthlyRent;
    }

    public void setAvgMonthlyRent(Double avgMonthlyRent) {
        this.avgMonthlyRent = avgMonthlyRent;
    }

    public Double getMedianDeposit() {
        return medianDeposit;
    }

    public void setMedianDeposit(Double medianDeposit) {
        this.medianDeposit = medianDeposit;
    }

    public Double getMedianMonthlyRent() {
        return medianMonthlyRent;
    }

    public void setMedianMonthlyRent(Double medianMonthlyRent) {
        this.medianMonthlyRent = medianMonthlyRent;
    }

    public Integer getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getRecentTransactionDate() {
        return recentTransactionDate;
    }

    public void setRecentTransactionDate(String recentTransactionDate) {
        this.recentTransactionDate = recentTransactionDate;
    }
}