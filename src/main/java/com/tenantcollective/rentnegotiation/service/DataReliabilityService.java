package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.DataReliability;
import com.tenantcollective.rentnegotiation.model.DiagnosisStats;
import com.tenantcollective.rentnegotiation.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DataReliabilityService {
    
    private final DiagnosisService diagnosisService;
    
    // 신뢰도 기준값들
    private static final int MIN_BUILDING_PARTICIPANTS = 3;
    private static final int MIN_NEIGHBORHOOD_PARTICIPANTS = 10;
    private static final int IDEAL_BUILDING_PARTICIPANTS = 10;
    private static final int IDEAL_NEIGHBORHOOD_PARTICIPANTS = 50;
    
    @Autowired
    public DataReliabilityService(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }
    
    /**
     * 사용자의 데이터 신뢰도를 평가
     */
    public DataReliability evaluateDataReliability(String userId) {
        try {
            DiagnosisStats stats = diagnosisService.getComparisonStats(userId);
            
            int buildingCount = stats.getBuildingParticipantCount();
            int neighborhoodCount = stats.getNeighborhoodParticipantCount();
            
            // 신뢰도 점수 계산 (0-100점)
            double buildingScore = calculateReliabilityScore(buildingCount, MIN_BUILDING_PARTICIPANTS, IDEAL_BUILDING_PARTICIPANTS);
            double neighborhoodScore = calculateReliabilityScore(neighborhoodCount, MIN_NEIGHBORHOOD_PARTICIPANTS, IDEAL_NEIGHBORHOOD_PARTICIPANTS);
            
            // 리포트 생성 가능 여부 판단
            boolean isEligible = buildingCount >= MIN_BUILDING_PARTICIPANTS && neighborhoodCount >= MIN_NEIGHBORHOOD_PARTICIPANTS;
            
            // 신뢰도 메시지 생성
            String message = generateReliabilityMessage(buildingCount, neighborhoodCount, buildingScore, neighborhoodScore, isEligible);
            
            // 카테고리별 참여자 수 (실제로는 더 상세한 데이터가 필요)
            Map<String, Integer> categoryCounts = new HashMap<>();
            categoryCounts.put("건물", buildingCount);
            categoryCounts.put("동네", neighborhoodCount);
            
            return new DataReliability(
                buildingCount,
                neighborhoodCount,
                buildingScore,
                neighborhoodScore,
                isEligible,
                message
            );
            
        } catch (Exception e) {
            System.err.println("Error evaluating data reliability: " + e.getMessage());
            return new DataReliability(
                0, 0, 0.0, 0.0, false,
                "데이터 신뢰도 평가 중 오류가 발생했습니다."
            );
        }
    }
    
    /**
     * 신뢰도 점수 계산 (0-100점)
     */
    private double calculateReliabilityScore(int participantCount, int minRequired, int idealCount) {
        if (participantCount < minRequired) {
            return 0.0;
        }
        
        if (participantCount >= idealCount) {
            return 100.0;
        }
        
        // 최소값과 이상값 사이에서 선형 보간
        double ratio = (double) (participantCount - minRequired) / (idealCount - minRequired);
        return Math.min(100.0, 30.0 + (ratio * 70.0)); // 최소 30점부터 시작
    }
    
    /**
     * 신뢰도 메시지 생성
     */
    private String generateReliabilityMessage(int buildingCount, int neighborhoodCount, 
                                            double buildingScore, double neighborhoodScore, 
                                            boolean isEligible) {
        if (!isEligible) {
            if (buildingCount < MIN_BUILDING_PARTICIPANTS) {
                return String.format("리포트 생성에 필요한 데이터가 부족합니다. 같은 건물에 최소 %d명 이상의 참여가 필요합니다. (현재: %d명)", 
                    MIN_BUILDING_PARTICIPANTS, buildingCount);
            } else {
                return String.format("리포트 생성에 필요한 데이터가 부족합니다. 같은 동네에 최소 %d명 이상의 참여가 필요합니다. (현재: %d명)", 
                    MIN_NEIGHBORHOOD_PARTICIPANTS, neighborhoodCount);
            }
        }
        
        if (buildingScore >= 80 && neighborhoodScore >= 80) {
            return String.format("데이터 신뢰도가 매우 높습니다! (건물: %.0f점, 동네: %.0f점) 신뢰할 수 있는 리포트를 생성할 수 있습니다.", 
                buildingScore, neighborhoodScore);
        } else if (buildingScore >= 60 && neighborhoodScore >= 60) {
            return String.format("데이터 신뢰도가 양호합니다. (건물: %.0f점, 동네: %.0f점) 참고용 리포트를 생성할 수 있습니다.", 
                buildingScore, neighborhoodScore);
        } else {
            return String.format("데이터 신뢰도가 낮습니다. (건물: %.0f점, 동네: %.0f점) 더 많은 참여자가 필요합니다.", 
                buildingScore, neighborhoodScore);
        }
    }
    
    /**
     * 리포트 생성 가능 여부만 확인
     */
    public boolean isReportGenerationEligible(String userId) {
        DataReliability reliability = evaluateDataReliability(userId);
        return reliability.isReportEligible();
    }
}