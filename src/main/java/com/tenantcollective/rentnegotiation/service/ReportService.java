package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.*;
import com.tenantcollective.rentnegotiation.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final UserRepository userRepository;
    private final DiagnosisService diagnosisService;
    private final RealEstateApiService realEstateApiService;
    private final DataReliabilityService dataReliabilityService;
    private final IssueClassificationService issueClassificationService;
    private final NegotiationPriorityService negotiationPriorityService;

    @Autowired
    public ReportService(UserRepository userRepository, DiagnosisService diagnosisService, 
                        RealEstateApiService realEstateApiService, DataReliabilityService dataReliabilityService,
                        IssueClassificationService issueClassificationService, NegotiationPriorityService negotiationPriorityService) {
        this.userRepository = userRepository;
        this.diagnosisService = diagnosisService;
        this.realEstateApiService = realEstateApiService;
        this.dataReliabilityService = dataReliabilityService;
        this.issueClassificationService = issueClassificationService;
        this.negotiationPriorityService = negotiationPriorityService;
    }

    public AdvancedReport generateAdvancedReport(String userId) {
        // 1. Fetch all necessary data
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 2. Validate data reliability using the new service
        DataReliability reliability = dataReliabilityService.evaluateDataReliability(userId);
        if (!reliability.isReportEligible()) {
            throw new IllegalStateException(reliability.getReliabilityMessage());
        }

        DiagnosisStats diagnosisStats = diagnosisService.getComparisonStats(userId);

        // 3. Fetch other data
        MarketData marketData = realEstateApiService.fetchMarketData(user.getNeighborhood());

        // 4. Generate prioritized negotiation cards using the new system
        List<NegotiationCard> negotiationCards = negotiationPriorityService.calculatePrioritizedCards(diagnosisStats);
        
        // 5. Convert negotiation cards to legacy format for backward compatibility
        List<NegotiationTip> strategies = convertCardsToTips(negotiationCards);

        // 6. Generate key findings and recommendations
        List<String> keyFindings = generateKeyFindings(user, marketData, diagnosisStats, reliability);
        List<String> recommendations = generateRecommendations(strategies, diagnosisStats, reliability);

        // 7. Assemble the final report
        AdvancedReport report = new AdvancedReport();
        report.setUserProfile(user);
        report.setMarketData(marketData);
        report.setDiagnosisStats(diagnosisStats);
        report.setNegotiationStrategies(strategies);
        report.setDataReliability(reliability);
        report.setKeyFindings(keyFindings);
        report.setRecommendations(recommendations);

        return report;
    }

    private List<NegotiationTip> createNegotiationStrategies(User user, MarketData marketData, DiagnosisStats diagnosisStats) {
        List<NegotiationTip> tips = new ArrayList<>();

        // Strategy 1: Compare rent with market data
        // TODO: 월세 정보는 Tenant 모델에서 가져와야 함
        // if (marketData != null && marketData.getAvgMonthlyRent() > 0) {
        //     double marketRent = marketData.getAvgMonthlyRent() * 10000; // Convert 만원 to 원
        //     double userRent = user.getMonthlyRent();
        //     if (userRent > marketRent * 1.1) { // If user's rent is 10% higher than market average
        //         tips.add(new NegotiationTip(
        //                 "RENT_RATE",
        //                 String.format("현재 월세(%.0f원)는 주변 평균(%.0f원)보다 10%% 이상 높습니다. 시세 리포트를 근거로 평균 수준으로 조정을 제안해 보세요.", userRent, marketRent),
        //                 1 // High priority
        //         ));
        //     }
        // }

        // Strategy 2: Analyze diagnosis results
        if (diagnosisStats != null && diagnosisStats.getUserScores() != null && diagnosisStats.getNeighborhoodAverageScores() != null) {
            Map<String, Double> userScores = diagnosisStats.getUserScores();
            Map<String, Double> neighborhoodScores = diagnosisStats.getNeighborhoodAverageScores();

            for (Map.Entry<String, Double> entry : userScores.entrySet()) {
                String questionId = entry.getKey();
                Double userScore = entry.getValue();
                Double neighborhoodAvg = neighborhoodScores.get(questionId);

                if (neighborhoodAvg != null && userScore < neighborhoodAvg * 0.8) { // If user's score is 20% lower than average
                    tips.add(new NegotiationTip(
                            "FACILITIES",
                            String.format("'%s' 항목의 만족도가 동네 평균보다 현저히 낮습니다. 월세 인하가 어렵다면, 이 문제에 대한 시설 개선(수리, 교체)을 요구해보세요.", formatQuestionId(questionId)),
                            2 // Medium priority
                    ));
                }
            }
        }

        // Add a default tip if no other tips were generated
        if (tips.isEmpty()) {
            tips.add(new NegotiationTip(
                    "GENERAL",
                    "리포트의 데이터를 활용하여 임대인과 긍정적인 소통을 시작해보세요. 작은 부분이라도 개선을 요청하는 것이 중요합니다.",
                    3 // Low priority
            ));
        }

        return tips;
    }

    /**
     * 협상 카드를 기존 NegotiationTip 형식으로 변환 (하위 호환성)
     */
    private List<NegotiationTip> convertCardsToTips(List<NegotiationCard> cards) {
        List<NegotiationTip> tips = new ArrayList<>();
        
        for (NegotiationCard card : cards) {
            String tipType = card.getCategory().name();
            String tipMessage = String.format("%s - %s", card.getDescription(), card.getNegotiationStrategy());
            int priority = card.getPriority();
            
            tips.add(new NegotiationTip(tipType, tipMessage, priority));
        }
        
        // 카드가 없는 경우 기본 팁 추가
        if (tips.isEmpty()) {
            tips.add(new NegotiationTip(
                    "GENERAL",
                    "리포트의 데이터를 활용하여 임대인과 긍정적인 소통을 시작해보세요. 작은 부분이라도 개선을 요청하는 것이 중요합니다.",
                    3
            ));
        }
        
        return tips;
    }
    
    /**
     * 협상 카드 목록을 직접 반환하는 메서드 (새로운 API용)
     */
    public List<NegotiationCard> getNegotiationCards(String userId) {
        // 신뢰도 검증
        DataReliability reliability = dataReliabilityService.evaluateDataReliability(userId);
        if (!reliability.isReportEligible()) {
            throw new IllegalStateException(reliability.getReliabilityMessage());
        }
        
        DiagnosisStats diagnosisStats = diagnosisService.getComparisonStats(userId);
        return negotiationPriorityService.calculatePrioritizedCards(diagnosisStats);
    }
    
    /**
     * 협상 전략 시나리오 생성
     */
    public String generateNegotiationScenario(String userId) {
        List<NegotiationCard> cards = getNegotiationCards(userId);
        return negotiationPriorityService.generateNegotiationScenario(cards);
    }
    
    /**
     * 협상 성공 확률 추정
     */
    public double estimateNegotiationSuccessRate(String userId) {
        List<NegotiationCard> cards = getNegotiationCards(userId);
        return negotiationPriorityService.estimateNegotiationSuccessRate(cards);
    }
    
    /**
     * 주요 발견사항 생성
     */
    private List<String> generateKeyFindings(User user, MarketData marketData, DiagnosisStats diagnosisStats, DataReliability reliability) {
        List<String> findings = new ArrayList<>();
        
        // 1. 시장 데이터 기반 발견사항
        if (marketData != null) {
            findings.add(String.format("%s 지역의 평균 월세는 %,d원으로, 시장 데이터가 충분히 확보되어 있습니다.", 
                marketData.getNeighborhood(), marketData.getAvgMonthlyRent()));
        }
        
        // 2. 진단 통계 기반 발견사항
        if (diagnosisStats != null && diagnosisStats.getUserScores() != null) {
            Map<String, Double> userScores = diagnosisStats.getUserScores();
            
            // 가장 낮은 점수 항목 찾기
            String lowestCategory = null;
            double lowestScore = Double.MAX_VALUE;
            for (Map.Entry<String, Double> entry : userScores.entrySet()) {
                if (entry.getValue() < lowestScore) {
                    lowestScore = entry.getValue();
                    lowestCategory = entry.getKey();
                }
            }
            
            if (lowestCategory != null && lowestScore < 60) {
                findings.add(String.format("%s 항목의 만족도가 낮습니다 (%.0f점). 이는 협상 시 주요 근거로 활용할 수 있습니다.", 
                    formatQuestionId(lowestCategory), lowestScore));
            }
            
            // 동네 평균과 비교
            if (diagnosisStats.getNeighborhoodAverageScores() != null) {
                Map<String, Double> neighborhoodScores = diagnosisStats.getNeighborhoodAverageScores();
                for (Map.Entry<String, Double> entry : userScores.entrySet()) {
                    String category = entry.getKey();
                    Double userScore = entry.getValue();
                    Double neighborhoodAvg = neighborhoodScores.get(category);
                    
                    if (neighborhoodAvg != null && userScore < neighborhoodAvg * 0.8) {
                        findings.add(String.format("%s 항목에서 동네 평균(%.0f점)보다 현저히 낮은 점수(%.0f점)를 기록했습니다.", 
                            formatQuestionId(category), neighborhoodAvg, userScore));
                    }
                }
            }
        }
        
        // 3. 데이터 신뢰도 기반 발견사항
        if (reliability != null) {
            if (reliability.getBuildingParticipantCount() > 0) {
                findings.add(String.format("같은 건물에서 %d명이 참여하여 신뢰할 수 있는 비교 데이터를 제공합니다.", 
                    reliability.getBuildingParticipantCount()));
            }
            
            if (reliability.getNeighborhoodParticipantCount() > 0) {
                findings.add(String.format("같은 동네에서 %d명이 참여하여 지역별 비교 분석이 가능합니다.", 
                    reliability.getNeighborhoodParticipantCount()));
            }
        }
        
        // 4. 기본 발견사항 (데이터가 부족한 경우)
        if (findings.isEmpty()) {
            findings.add("종합적인 거주 환경 분석이 완료되었습니다.");
            findings.add("수집된 데이터를 바탕으로 협상 전략을 수립할 수 있습니다.");
        }
        
        return findings;
    }
    
    /**
     * 추천사항 생성
     */
    private List<String> generateRecommendations(List<NegotiationTip> strategies, DiagnosisStats diagnosisStats, DataReliability reliability) {
        List<String> recommendations = new ArrayList<>();
        
        // 1. 협상 전략 기반 추천사항
        if (strategies != null && !strategies.isEmpty()) {
            // 우선순위가 높은 전략들을 추천사항으로 변환
            strategies.stream()
                .filter(strategy -> strategy.getPriority() <= 2) // 높은 우선순위만
                .limit(3) // 최대 3개
                .forEach(strategy -> {
                    recommendations.add(strategy.getMessage());
                });
        }
        
        // 2. 데이터 신뢰도 기반 추천사항
        if (reliability != null) {
            if (!reliability.isReportEligible()) {
                recommendations.add(reliability.getReliabilityMessage());
            } else {
                recommendations.add("충분한 데이터가 확보되어 신뢰할 수 있는 협상 근거를 제시할 수 있습니다.");
            }
        }
        
        // 3. 진단 통계 기반 추천사항
        if (diagnosisStats != null && diagnosisStats.getUserScores() != null) {
            Map<String, Double> userScores = diagnosisStats.getUserScores();
            long lowScoreCount = userScores.values().stream()
                .mapToLong(score -> score < 60 ? 1 : 0)
                .sum();
            
            if (lowScoreCount > 0) {
                recommendations.add(String.format("%d개 항목에서 개선이 필요합니다. 이 항목들을 우선적으로 협상에 활용하세요.", lowScoreCount));
            }
        }
        
        // 4. 기본 추천사항
        if (recommendations.isEmpty()) {
            recommendations.add("데이터 기반의 객관적 근거를 제시하여 협상력을 높이세요.");
            recommendations.add("정중하고 전문적인 태도로 임대인과 소통하세요.");
            recommendations.add("서면으로 합의사항을 정리하고 보관하세요.");
        }
        
        return recommendations;
    }

    private String formatQuestionId(String questionId) {
        switch (questionId) {
            case "noise_level": return "소음";
            case "water_pressure": return "수압";
            case "sunlight": return "채광";
            case "parking": return "주차";
            case "heating": return "난방";
            case "security": return "보안";
            case "elevator": return "엘리베이터";
            case "facilities": return "기타 시설";
            default: return questionId;
        }
    }
}
