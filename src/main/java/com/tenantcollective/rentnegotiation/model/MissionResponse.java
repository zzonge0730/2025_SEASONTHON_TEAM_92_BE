package com.tenantcollective.rentnegotiation.model;

import java.time.LocalDateTime;

public class MissionResponse {
    private String id;
    private String missionId;
    private String userId;
    private String buildingName;
    private String neighborhood;
    private String questionId;
    private String answer;
    private int score;
    private LocalDateTime submittedAt;

    // Constructors
    public MissionResponse() {
        this.submittedAt = LocalDateTime.now();
    }

    public MissionResponse(String missionId, String userId, String buildingName, String neighborhood, String questionId, String answer, int score) {
        this();
        this.missionId = missionId;
        this.userId = userId;
        this.buildingName = buildingName;
        this.neighborhood = neighborhood;
        this.questionId = questionId;
        this.answer = answer;
        this.score = score;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}