package com.tenantcollective.rentnegotiation.model;

public class MissionQuestion {
    private String id;
    private String question;
    private String type; // single_choice, multiple_choice, scale
    private String[] options;
    private int weight;
    private String category;

    // Constructors
    public MissionQuestion() {}

    public MissionQuestion(String id, String question, String type, String[] options, int weight, String category) {
        this.id = id;
        this.question = question;
        this.type = type;
        this.options = options;
        this.weight = weight;
        this.category = category;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}