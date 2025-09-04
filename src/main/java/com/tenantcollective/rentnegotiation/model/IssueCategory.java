package com.tenantcollective.rentnegotiation.model;

public enum IssueCategory {
    LEGAL_REPAIR("법적 수선 의무", "임대인이 법적으로 수선해야 하는 문제들", 1),
    STRUCTURAL("구조적 문제", "해결이 어려운 구조적 문제들", 2),
    GENERAL("일반적 문제", "협상 가능한 일반적 문제들", 3);
    
    private final String koreanName;
    private final String description;
    private final int priority;
    
    IssueCategory(String koreanName, String description, int priority) {
        this.koreanName = koreanName;
        this.description = description;
        this.priority = priority;
    }
    
    public String getKoreanName() {
        return koreanName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getPriority() {
        return priority;
    }
}