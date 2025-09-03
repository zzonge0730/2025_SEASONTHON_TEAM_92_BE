package com.tenantcollective.rentnegotiation.model;

public class VoteResult {
    private String proposalId;
    private int totalVotes;
    private int agreeVotes;
    private int disagreeVotes;
    private double agreePercentage;
    private double disagreePercentage;

    public VoteResult() {}

    public VoteResult(String proposalId, int totalVotes, int agreeVotes, int disagreeVotes) {
        this.proposalId = proposalId;
        this.totalVotes = totalVotes;
        this.agreeVotes = agreeVotes;
        this.disagreeVotes = disagreeVotes;
        this.agreePercentage = totalVotes > 0 ? (double) agreeVotes / totalVotes * 100 : 0;
        this.disagreePercentage = totalVotes > 0 ? (double) disagreeVotes / totalVotes * 100 : 0;
    }

    // Getters and Setters
    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getAgreeVotes() {
        return agreeVotes;
    }

    public void setAgreeVotes(int agreeVotes) {
        this.agreeVotes = agreeVotes;
    }

    public int getDisagreeVotes() {
        return disagreeVotes;
    }

    public void setDisagreeVotes(int disagreeVotes) {
        this.disagreeVotes = disagreeVotes;
    }

    public double getAgreePercentage() {
        return agreePercentage;
    }

    public void setAgreePercentage(double agreePercentage) {
        this.agreePercentage = agreePercentage;
    }

    public double getDisagreePercentage() {
        return disagreePercentage;
    }

    public void setDisagreePercentage(double disagreePercentage) {
        this.disagreePercentage = disagreePercentage;
    }
}
