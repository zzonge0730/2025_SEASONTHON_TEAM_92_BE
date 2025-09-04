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

    @Autowired
    public ReportService(UserRepository userRepository, DiagnosisService diagnosisService, RealEstateApiService realEstateApiService) {
        this.userRepository = userRepository;
        this.diagnosisService = diagnosisService;
        this.realEstateApiService = realEstateApiService;
    }

    private static final int MIN_BUILDING_PARTICIPANTS = 3;

    public AdvancedReport generateAdvancedReport(String userId) {
        // 1. Fetch all necessary data
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        DiagnosisStats diagnosisStats = diagnosisService.getComparisonStats(userId);

        // 2. Validate data reliability
        if (diagnosisStats.getBuildingParticipantCount() < MIN_BUILDING_PARTICIPANTS) {
            throw new IllegalStateException("리포트를 생성하기에 데이터가 부족합니다. 같은 건물에 최소 " + MIN_BUILDING_PARTICIPANTS + "명 이상의 참여가 필요합니다.");
        }

        // 3. Fetch other data
        MarketData marketData = realEstateApiService.fetchMarketData(user.getNeighborhood());

        // 4. Generate negotiation strategies based on the data
        List<NegotiationTip> strategies = createNegotiationStrategies(user, marketData, diagnosisStats);

        // 5. Assemble the final report
        AdvancedReport report = new AdvancedReport();
        report.setUserProfile(user);
        report.setMarketData(marketData);
        report.setDiagnosisStats(diagnosisStats);
        report.setNegotiationStrategies(strategies);

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

    private String formatQuestionId(String questionId) {
        switch (questionId) {
            case "noise_level": return "소음";
            case "water_pressure": return "수압";
            case "sunlight": return "채광";
            case "parking": return "주차";
            case "heating": return "난방";
            default: return questionId;
        }
    }
}
