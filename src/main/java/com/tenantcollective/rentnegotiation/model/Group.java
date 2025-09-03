package com.tenantcollective.rentnegotiation.model;

public class Group {
    private String groupId;
    private String label;
    private String scope;
    private Integer groupSize;
    private Double avgRentKrw;
    private Double medianRentKrw;
    private Double avgNoticePct;
    private MarketData marketData;

    public Group() {}

    public Group(String groupId, String label, String scope, Integer groupSize,
                 Double avgRentKrw, Double medianRentKrw, Double avgNoticePct) {
        this.groupId = groupId;
        this.label = label;
        this.scope = scope;
        this.groupSize = groupSize;
        this.avgRentKrw = avgRentKrw;
        this.medianRentKrw = medianRentKrw;
        this.avgNoticePct = avgNoticePct;
    }

    public Group(String groupId, String label, String scope, Integer groupSize,
                 Double avgRentKrw, Double medianRentKrw, Double avgNoticePct, MarketData marketData) {
        this.groupId = groupId;
        this.label = label;
        this.scope = scope;
        this.groupSize = groupSize;
        this.avgRentKrw = avgRentKrw;
        this.medianRentKrw = medianRentKrw;
        this.avgNoticePct = avgNoticePct;
        this.marketData = marketData;
    }

    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }

    public Double getAvgRentKrw() {
        return avgRentKrw;
    }

    public void setAvgRentKrw(Double avgRentKrw) {
        this.avgRentKrw = avgRentKrw;
    }

    public Double getMedianRentKrw() {
        return medianRentKrw;
    }

    public void setMedianRentKrw(Double medianRentKrw) {
        this.medianRentKrw = medianRentKrw;
    }

    public Double getAvgNoticePct() {
        return avgNoticePct;
    }

    public void setAvgNoticePct(Double avgNoticePct) {
        this.avgNoticePct = avgNoticePct;
    }

    public MarketData getMarketData() {
        return marketData;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }
}