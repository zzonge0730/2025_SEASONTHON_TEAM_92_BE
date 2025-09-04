package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IssueClassificationService {
    
    // 질문 ID와 이슈 카테고리 매핑
    private static final Map<String, IssueCategory> ISSUE_CATEGORY_MAP = new HashMap<>();
    private static final Map<String, String> ISSUE_NAMES = new HashMap<>();
    private static final Map<String, String> LEGAL_BASIS_MAP = new HashMap<>();
    
    static {
        // 법적 수선 의무 문제들
        ISSUE_CATEGORY_MAP.put("water_pressure", IssueCategory.LEGAL_REPAIR);
        ISSUE_NAMES.put("water_pressure", "수압 문제");
        LEGAL_BASIS_MAP.put("water_pressure", "임대차법상 임대인의 수선 의무");
        
        ISSUE_CATEGORY_MAP.put("heating", IssueCategory.LEGAL_REPAIR);
        ISSUE_NAMES.put("heating", "난방 시설");
        LEGAL_BASIS_MAP.put("heating", "임대차법상 난방 시설 유지 의무");
        
        ISSUE_CATEGORY_MAP.put("mold", IssueCategory.LEGAL_REPAIR);
        ISSUE_NAMES.put("mold", "곰팡이 문제");
        LEGAL_BASIS_MAP.put("mold", "임대차법상 쾌적한 거주 환경 제공 의무");
        
        ISSUE_CATEGORY_MAP.put("door_lock", IssueCategory.LEGAL_REPAIR);
        ISSUE_NAMES.put("door_lock", "도어락 고장");
        LEGAL_BASIS_MAP.put("door_lock", "임대차법상 안전 시설 유지 의무");
        
        ISSUE_CATEGORY_MAP.put("boiler", IssueCategory.LEGAL_REPAIR);
        ISSUE_NAMES.put("boiler", "보일러 고장");
        LEGAL_BASIS_MAP.put("boiler", "임대차법상 난방 시설 수선 의무");
        
        ISSUE_CATEGORY_MAP.put("leak", IssueCategory.LEGAL_REPAIR);
        ISSUE_NAMES.put("leak", "누수 문제");
        LEGAL_BASIS_MAP.put("leak", "임대차법상 건물 구조 유지 의무");
        
        // 구조적 문제들
        ISSUE_CATEGORY_MAP.put("noise_level", IssueCategory.STRUCTURAL);
        ISSUE_NAMES.put("noise_level", "소음 문제");
        LEGAL_BASIS_MAP.put("noise_level", "구조적 문제로 해결 어려움");
        
        ISSUE_CATEGORY_MAP.put("sunlight", IssueCategory.STRUCTURAL);
        ISSUE_NAMES.put("sunlight", "채광 부족");
        LEGAL_BASIS_MAP.put("sunlight", "건물 구조상 개선 한계");
        
        ISSUE_CATEGORY_MAP.put("view", IssueCategory.STRUCTURAL);
        ISSUE_NAMES.put("view", "전망 문제");
        LEGAL_BASIS_MAP.put("view", "건물 위치상 개선 불가");
        
        ISSUE_CATEGORY_MAP.put("floor_noise", IssueCategory.STRUCTURAL);
        ISSUE_NAMES.put("floor_noise", "층간소음");
        LEGAL_BASIS_MAP.put("floor_noise", "건물 구조상 완전 해결 어려움");
        
        // 일반적 문제들
        ISSUE_CATEGORY_MAP.put("parking", IssueCategory.GENERAL);
        ISSUE_NAMES.put("parking", "주차 불편");
        LEGAL_BASIS_MAP.put("parking", "협상을 통한 개선 가능");
        
        ISSUE_CATEGORY_MAP.put("elevator", IssueCategory.GENERAL);
        ISSUE_NAMES.put("elevator", "엘리베이터 불편");
        LEGAL_BASIS_MAP.put("elevator", "시설 개선 협상 가능");
        
        ISSUE_CATEGORY_MAP.put("security", IssueCategory.GENERAL);
        ISSUE_NAMES.put("security", "보안 시설");
        LEGAL_BASIS_MAP.put("security", "보안 강화 협상 가능");
    }
    
    /**
     * 진단 결과를 기반으로 협상 카드들을 생성
     */
    public List<NegotiationCard> generateNegotiationCards(DiagnosisStats diagnosisStats) {
        List<NegotiationCard> cards = new ArrayList<>();
        
        if (diagnosisStats == null || diagnosisStats.getUserScores() == null || 
            diagnosisStats.getNeighborhoodAverageScores() == null) {
            return cards;
        }
        
        Map<String, Double> userScores = diagnosisStats.getUserScores();
        Map<String, Double> neighborhoodScores = diagnosisStats.getNeighborhoodAverageScores();
        
        for (Map.Entry<String, Double> entry : userScores.entrySet()) {
            String questionId = entry.getKey();
            Double userScore = entry.getValue();
            Double neighborhoodAvg = neighborhoodScores.get(questionId);
            
            if (neighborhoodAvg != null && userScore < neighborhoodAvg * 0.8) {
                // 사용자 점수가 동네 평균의 80% 미만인 경우에만 협상 카드 생성
                double scoreDifference = neighborhoodAvg - userScore;
                NegotiationCard card = createNegotiationCard(questionId, userScore, neighborhoodAvg, scoreDifference);
                if (card != null) {
                    cards.add(card);
                }
            }
        }
        
        // 우선순위별로 정렬 (법적 수선 의무 > 구조적 문제 > 일반적 문제)
        return cards.stream()
                .sorted(Comparator.comparing(NegotiationCard::getCategory, 
                        Comparator.comparing(IssueCategory::getPriority))
                        .thenComparing(NegotiationCard::getScoreDifference, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
    
    /**
     * 개별 협상 카드 생성
     */
    private NegotiationCard createNegotiationCard(String questionId, double userScore, 
                                                 double neighborhoodAvg, double scoreDifference) {
        IssueCategory category = ISSUE_CATEGORY_MAP.get(questionId);
        if (category == null) {
            return null;
        }
        
        String issueName = ISSUE_NAMES.getOrDefault(questionId, questionId);
        String legalBasis = LEGAL_BASIS_MAP.getOrDefault(questionId, "협상 가능한 사항");
        
        String description = String.format("현재 %s 만족도(%.1f점)는 동네 평균(%.1f점)보다 %.1f점 낮습니다.", 
                issueName, userScore, neighborhoodAvg, scoreDifference);
        
        String negotiationStrategy = generateNegotiationStrategy(category, issueName, scoreDifference);
        String suggestedAction = generateSuggestedAction(category, issueName);
        
        return new NegotiationCard(
                questionId,
                issueName,
                category,
                description,
                negotiationStrategy,
                category.getPriority(),
                scoreDifference,
                legalBasis,
                suggestedAction
        );
    }
    
    /**
     * 카테고리별 협상 전략 생성
     */
    private String generateNegotiationStrategy(IssueCategory category, String issueName, double scoreDifference) {
        switch (category) {
            case LEGAL_REPAIR:
                return String.format("'%s'은(는) 임대인의 법적 수선 의무에 해당합니다. 이 데이터를 근거로 즉시 수선을 요구하세요. 만약 수선이 지연될 경우 월세 인상률을 동네 평균보다 낮게 조정해달라고 제안할 수 있습니다.", issueName);
            
            case STRUCTURAL:
                return String.format("'%s'은(는) 구조적 문제로 완전한 해결이 어렵습니다. 이 데이터를 근거로 재계약 시 월세 인상률을 동네 평균(3%%)보다 낮은 1.5%%로 조정해달라고 요구하세요.", issueName);
            
            case GENERAL:
                return String.format("'%s' 문제를 개선할 수 있도록 임대인과 협의해보세요. 개선이 어렵다면 월세 인상률 조정을 대안으로 제시할 수 있습니다.", issueName);
            
            default:
                return "데이터를 바탕으로 임대인과 긍정적인 소통을 시작해보세요.";
        }
    }
    
    /**
     * 카테고리별 제안 행동 생성
     */
    private String generateSuggestedAction(IssueCategory category, String issueName) {
        switch (category) {
            case LEGAL_REPAIR:
                return String.format("1순위: '%s' 즉시 수선 요구 → 2순위: 수선 지연 시 월세 인상률 조정 제안", issueName);
            
            case STRUCTURAL:
                return String.format("월세 인상률을 동네 평균보다 낮게 조정해달라고 요구 (구조적 문제로 해결 어려움을 근거로 제시)", issueName);
            
            case GENERAL:
                return String.format("'%s' 개선 방안 협의 → 개선 어려울 경우 월세 인상률 조정 대안 제시", issueName);
            
            default:
                return "임대인과 협상을 통한 개선 방안 모색";
        }
    }
    
    /**
     * 특정 카테고리의 협상 카드만 필터링
     */
    public List<NegotiationCard> getCardsByCategory(List<NegotiationCard> cards, IssueCategory category) {
        return cards.stream()
                .filter(card -> card.getCategory() == category)
                .collect(Collectors.toList());
    }
    
    /**
     * 최우선 협상 카드 (법적 수선 의무) 반환
     */
    public Optional<NegotiationCard> getTopPriorityCard(List<NegotiationCard> cards) {
        return cards.stream()
                .filter(card -> card.getCategory() == IssueCategory.LEGAL_REPAIR)
                .max(Comparator.comparing(NegotiationCard::getScoreDifference));
    }
}