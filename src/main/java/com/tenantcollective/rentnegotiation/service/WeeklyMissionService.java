package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeeklyMissionService {

    private final UserService userService;
    private final DiagnosisService diagnosisService;

    @Autowired
    public WeeklyMissionService(UserService userService, DiagnosisService diagnosisService) {
        this.userService = userService;
        this.diagnosisService = diagnosisService;
    }

    /**
     * 현재 활성화된 주간 미션 가져오기
     */
    public WeeklyMission getCurrentMission() {
        // 실제로는 데이터베이스에서 가져오지만, 여기서는 하드코딩된 미션을 반환
        return createSampleMission();
    }

    /**
     * 주간 미션 참여
     */
    public Map<String, Object> participateInMission(String userId, Map<String, String> answers) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

            WeeklyMission mission = getCurrentMission();
            
            // 미션 응답 저장 (실제로는 데이터베이스에 저장)
            List<MissionResponse> responses = new ArrayList<>();
            for (Map.Entry<String, String> entry : answers.entrySet()) {
                MissionResponse response = new MissionResponse(
                    mission.getId(),
                    userId,
                    user.getBuildingName(),
                    user.getNeighborhood(),
                    entry.getKey(),
                    entry.getValue(),
                    calculateScore(entry.getValue())
                );
                responses.add(response);
            }

            // 즉시 비교 분석 결과 생성
            Map<String, Object> comparisonResult = generateImmediateComparison(user, mission, responses);
            
            // 기존 진단 데이터에 반영
            updateDiagnosisData(user, responses);

            return comparisonResult;
        } catch (Exception e) {
            throw new RuntimeException("Failed to participate in mission: " + e.getMessage());
        }
    }

    /**
     * 즉시 비교 분석 결과 생성
     */
    private Map<String, Object> generateImmediateComparison(User user, WeeklyMission mission, List<MissionResponse> responses) {
        Map<String, Object> result = new HashMap<>();
        
        // 사용자 점수 계산
        double userScore = responses.stream()
                .mapToInt(MissionResponse::getScore)
                .average()
                .orElse(0.0);

        // 가상의 건물/동네 평균 점수 (실제로는 데이터베이스에서 계산)
        double buildingAverage = generateRandomScore(userScore, 0.8, 1.2);
        double neighborhoodAverage = generateRandomScore(userScore, 0.7, 1.3);
        
        // 순위 계산
        int buildingRank = calculateRank(userScore, buildingAverage);
        int neighborhoodRank = calculateRank(userScore, neighborhoodAverage);

        result.put("userScore", Math.round(userScore * 10) / 10.0);
        result.put("buildingAverage", Math.round(buildingAverage * 10) / 10.0);
        result.put("neighborhoodAverage", Math.round(neighborhoodAverage * 10) / 10.0);
        result.put("buildingRank", buildingRank);
        result.put("neighborhoodRank", neighborhoodRank);
        result.put("buildingParticipantCount", 12 + (int)(Math.random() * 8)); // 12-19명
        result.put("neighborhoodParticipantCount", 45 + (int)(Math.random() * 15)); // 45-59명
        result.put("missionTitle", mission.getTitle());
        result.put("category", mission.getCategory());
        result.put("submittedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        return result;
    }

    /**
     * 기존 진단 데이터에 미션 결과 반영
     */
    private void updateDiagnosisData(User user, List<MissionResponse> responses) {
        // 실제로는 기존 진단 데이터를 업데이트하거나 새로운 진단 응답을 생성
        // 여기서는 로그만 출력
        System.out.println("🔄 주간 미션 결과를 기존 진단 데이터에 반영 중...");
        System.out.println("👤 사용자: " + user.getEmail());
        System.out.println("📊 응답 수: " + responses.size());
        
        for (MissionResponse response : responses) {
            System.out.println("  - " + response.getQuestionId() + ": " + response.getAnswer() + " (점수: " + response.getScore() + ")");
        }
    }

    /**
     * 샘플 주간 미션 생성
     */
    private WeeklyMission createSampleMission() {
        WeeklyMission mission = new WeeklyMission();
        mission.setId("mission_" + System.currentTimeMillis());
        mission.setTitle("이번 주 주간 미션: 방음 상태 체크");
        mission.setDescription("우리 집의 방음 상태를 체크해보세요! 이웃들과 비교해보고 개선점을 찾아보세요.");
        mission.setCategory("noise");
        mission.setStartDate(LocalDateTime.now().minusDays(2));
        mission.setEndDate(LocalDateTime.now().plusDays(5));
        mission.setActive(true);
        mission.setParticipantCount(156);

        List<MissionQuestion> questions = Arrays.asList(
            new MissionQuestion("noise_mission_1", "옆집에서 들리는 생활 소음이 얼마나 자주 들리나요?", "single_choice", 
                new String[]{"전혀 안 들림", "가끔 들림", "자주 들림", "매우 자주 들림"}, 3, "noise"),
            new MissionQuestion("noise_mission_2", "층간소음으로 인한 불편함을 얼마나 느끼시나요?", "single_choice", 
                new String[]{"전혀 불편하지 않음", "가끔 불편함", "자주 불편함", "매우 불편함"}, 4, "noise"),
            new MissionQuestion("noise_mission_3", "외부 소음(도로, 공사 등)이 집안에 얼마나 잘 들리나요?", "single_choice", 
                new String[]{"전혀 안 들림", "가끔 들림", "자주 들림", "매우 자주 들림"}, 2, "noise")
        );

        mission.setQuestions(questions);
        return mission;
    }

    /**
     * 답변을 점수로 변환
     */
    private int calculateScore(String answer) {
        Map<String, Integer> scoreMap = Map.of(
            "전혀 안 들림", 4, "전혀 불편하지 않음", 4,
            "가끔 들림", 3, "가끔 불편함", 3,
            "자주 들림", 2, "자주 불편함", 2,
            "매우 자주 들림", 1, "매우 불편함", 1
        );
        return scoreMap.getOrDefault(answer, 2);
    }

    /**
     * 랜덤 점수 생성 (사용자 점수 기준으로 ±20% 범위)
     */
    private double generateRandomScore(double userScore, double minRatio, double maxRatio) {
        double min = userScore * minRatio;
        double max = userScore * maxRatio;
        return min + (Math.random() * (max - min));
    }

    /**
     * 순위 계산 (1-10위)
     */
    private int calculateRank(double userScore, double average) {
        if (userScore > average * 1.2) return 1 + (int)(Math.random() * 3); // 1-3위
        else if (userScore > average) return 4 + (int)(Math.random() * 3); // 4-6위
        else return 7 + (int)(Math.random() * 4); // 7-10위
    }
}