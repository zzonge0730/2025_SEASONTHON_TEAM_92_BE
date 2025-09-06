package com.tenantcollective.rentnegotiation.model;

import java.time.LocalDateTime;
import java.util.List;

public class WeeklyMission {
    private String id;
    private String title;
    private String description;
    private String category; // noise, water_pressure, lighting, etc.
    private List<MissionQuestion> questions;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isActive;
    private int participantCount;
    private LocalDateTime createdAt;

    // Constructors
    public WeeklyMission() {
        this.createdAt = LocalDateTime.now();
    }

    public WeeklyMission(String title, String description, String category, List<MissionQuestion> questions) {
        this();
        this.title = title;
        this.description = description;
        this.category = category;
        this.questions = questions;
        this.isActive = true;
        this.participantCount = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<MissionQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<MissionQuestion> questions) {
        this.questions = questions;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getParticipantCount() {
        return participantCount;
    }

    public void setParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}