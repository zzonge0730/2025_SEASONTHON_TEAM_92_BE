package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NegotiationPriorityService {
    
    private final IssueClassificationService issueClassificationService;
    
    @Autowired
    public NegotiationPriorityService(IssueClassificationService issueClassificationService) {
        this.issueClassificationService = issueClassificationService;
    }
    
    /**
     * 협상 카드들의 우선순위를 정교하게 계산하고 정렬
     */
    public List<NegotiationCard> calculatePrioritizedCards(DiagnosisStats diagnosisStats) {
        List<NegotiationCard> cards = issueClassificationService.generateNegotiationCards(diagnosisStats);
        
        // 각 카드에 대해 정교한 우선순위 점수 계산
        for (NegotiationCard card : cards) {
            double priorityScore = calculatePriorityScore(card, diagnosisStats);
            card.setPriority((int) Math.round(priorityScore));
        }
        
        // 우선순위 점수 기준으로 정렬 (높은 점수 = 높은 우선순위)
        return cards.stream()
                .sorted(Comparator.comparing(NegotiationCard::getPriority, Comparator.reverseOrder())
                        .thenComparing(NegotiationCard::getScoreDifference, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
    
    /**
     * 개별 카드의 우선순위 점수 계산 (0-100점)
     */
    private double calculatePriorityScore(NegotiationCard card, DiagnosisStats diagnosisStats) {
        double baseScore = 0;
        
        // 1. 카테고리별 기본 점수 (40점 만점)
        baseScore += getCategoryBaseScore(card.getCategory());
        
        // 2. 점수 차이 기반 점수 (30점 만점)
        baseScore += getScoreDifferenceScore(card.getScoreDifference());
        
        // 3. 법적 근거 강도 점수 (20점 만점)
        baseScore += getLegalBasisScore(card.getCategory());
        
        // 4. 해결 가능성 점수 (10점 만점)
        baseScore += getSolvabilityScore(card.getCategory());
        
        return Math.min(100.0, baseScore);
    }
    
    /**
     * 카테고리별 기본 점수
     */
    private double getCategoryBaseScore(IssueCategory category) {
        switch (category) {
            case LEGAL_REPAIR:
                return 40.0; // 법적 수선 의무는 최고 우선순위
            case STRUCTURAL:
                return 25.0; // 구조적 문제는 중간 우선순위
            case GENERAL:
                return 15.0; // 일반적 문제는 낮은 우선순위
            default:
                return 10.0;
        }
    }
    
    /**
     * 점수 차이 기반 점수 (차이가 클수록 높은 점수)
     */
    private double getScoreDifferenceScore(double scoreDifference) {
        if (scoreDifference >= 3.0) {
            return 30.0; // 매우 큰 차이
        } else if (scoreDifference >= 2.0) {
            return 25.0; // 큰 차이
        } else if (scoreDifference >= 1.5) {
            return 20.0; // 중간 차이
        } else if (scoreDifference >= 1.0) {
            return 15.0; // 작은 차이
        } else {
            return 10.0; // 매우 작은 차이
        }
    }
    
    /**
     * 법적 근거 강도 점수
     */
    private double getLegalBasisScore(IssueCategory category) {
        switch (category) {
            case LEGAL_REPAIR:
                return 20.0; // 명확한 법적 근거
            case STRUCTURAL:
                return 10.0; // 제한적 법적 근거
            case GENERAL:
                return 5.0;  // 약한 법적 근거
            default:
                return 0.0;
        }
    }
    
    /**
     * 해결 가능성 점수 (해결 가능할수록 높은 점수)
     */
    private double getSolvabilityScore(IssueCategory category) {
        switch (category) {
            case LEGAL_REPAIR:
                return 10.0; // 법적 의무로 해결 가능
            case GENERAL:
                return 8.0;  // 협상을 통해 해결 가능
            case STRUCTURAL:
                return 3.0;  // 해결 어려움
            default:
                return 0.0;
        }
    }
    
    /**
     * 협상 전략 시나리오 생성
     */
    public String generateNegotiationScenario(List<NegotiationCard> prioritizedCards) {
        if (prioritizedCards.isEmpty()) {
            return "현재 데이터를 바탕으로 협상할 수 있는 특별한 이슈가 없습니다. 일반적인 협상을 진행하세요.";
        }
        
        StringBuilder scenario = new StringBuilder();
        scenario.append("📋 **협상 전략 시나리오**\n\n");
        
        // 최우선 카드 (법적 수선 의무)
        List<NegotiationCard> legalCards = issueClassificationService.getCardsByCategory(prioritizedCards, IssueCategory.LEGAL_REPAIR);
        if (!legalCards.isEmpty()) {
            scenario.append("🔥 **1순위: 법적 수선 의무 문제**\n");
            for (int i = 0; i < Math.min(2, legalCards.size()); i++) {
                NegotiationCard card = legalCards.get(i);
                scenario.append(String.format("• %s: %s\n", card.getIssueName(), card.getSuggestedAction()));
            }
            scenario.append("\n");
        }
        
        // 구조적 문제
        List<NegotiationCard> structuralCards = issueClassificationService.getCardsByCategory(prioritizedCards, IssueCategory.STRUCTURAL);
        if (!structuralCards.isEmpty()) {
            scenario.append("⚠️ **2순위: 구조적 문제 (월세 인상률 조정 요구)**\n");
            for (int i = 0; i < Math.min(2, structuralCards.size()); i++) {
                NegotiationCard card = structuralCards.get(i);
                scenario.append(String.format("• %s: 월세 인상률을 동네 평균보다 낮게 조정 요구\n", card.getIssueName()));
            }
            scenario.append("\n");
        }
        
        // 일반적 문제
        List<NegotiationCard> generalCards = issueClassificationService.getCardsByCategory(prioritizedCards, IssueCategory.GENERAL);
        if (!generalCards.isEmpty()) {
            scenario.append("💬 **3순위: 일반적 문제 (협상 가능)**\n");
            for (int i = 0; i < Math.min(2, generalCards.size()); i++) {
                NegotiationCard card = generalCards.get(i);
                scenario.append(String.format("• %s: 개선 방안 협의\n", card.getIssueName()));
            }
            scenario.append("\n");
        }
        
        // 종합 전략
        scenario.append("🎯 **종합 협상 전략**\n");
        if (!legalCards.isEmpty()) {
            scenario.append("1. 법적 수선 의무 문제를 최우선으로 제기하세요.\n");
            scenario.append("2. 수선이 지연될 경우 월세 인상률 조정을 대안으로 제시하세요.\n");
        }
        if (!structuralCards.isEmpty()) {
            scenario.append("3. 구조적 문제는 월세 인상률 조정의 근거로 활용하세요.\n");
        }
        if (!generalCards.isEmpty()) {
            scenario.append("4. 일반적 문제는 상호 협의를 통해 해결하세요.\n");
        }
        
        return scenario.toString();
    }
    
    /**
     * 협상 성공 확률 추정
     */
    public double estimateNegotiationSuccessRate(List<NegotiationCard> prioritizedCards) {
        if (prioritizedCards.isEmpty()) {
            return 30.0; // 기본 성공률
        }
        
        double totalScore = 0;
        double weightSum = 0;
        
        for (NegotiationCard card : prioritizedCards) {
            double weight = getCategoryWeight(card.getCategory());
            double successRate = getCategorySuccessRate(card.getCategory());
            
            totalScore += successRate * weight;
            weightSum += weight;
        }
        
        return weightSum > 0 ? totalScore / weightSum : 30.0;
    }
    
    private double getCategoryWeight(IssueCategory category) {
        switch (category) {
            case LEGAL_REPAIR: return 3.0;
            case STRUCTURAL: return 2.0;
            case GENERAL: return 1.0;
            default: return 0.5;
        }
    }
    
    private double getCategorySuccessRate(IssueCategory category) {
        switch (category) {
            case LEGAL_REPAIR: return 85.0; // 법적 근거가 있어 성공률 높음
            case STRUCTURAL: return 60.0;   // 구조적 문제로 부분적 성공
            case GENERAL: return 45.0;      // 협상에 따라 달라짐
            default: return 30.0;
        }
    }
}