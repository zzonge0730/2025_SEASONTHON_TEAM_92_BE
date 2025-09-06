package com.tenantcollective.rentnegotiation.mission.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.tenantcollective.rentnegotiation.mission.service.MissionService;
import com.tenantcollective.rentnegotiation.mission.dto.MissionRequestDTO;
import com.tenantcollective.rentnegotiation.mission.dto.MissionResponseDTO;
import com.tenantcollective.rentnegotiation.member.domain.Member;
import com.tenantcollective.rentnegotiation.auth.JwtTokenProvider;
import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.WeeklyMission;
import com.tenantcollective.rentnegotiation.model.MissionQuestion;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class WeeklyMissionController {

    private final MissionService missionService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 현재 활성화된 주간 미션 조회 (기존 API 호환)
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<WeeklyMission>> getCurrentMission() {
        try {
            // 임시로 기본 미션 반환 (기존 API 호환성을 위해)
            WeeklyMission mission = new WeeklyMission();
            mission.setId("1");
            mission.setTitle("주거환경 만족도 조사");
            mission.setDescription("거주하고 계신 건물의 주거환경에 대한 만족도를 평가해주세요.");
            // 임시 질문 데이터 생성
            MissionQuestion q1 = new MissionQuestion();
            q1.setId("1");
            q1.setQuestion("건물의 보안 상태는 어떤가요?");
            q1.setType("multiple_choice");
            q1.setOptions(new String[]{"매우 좋음", "좋음", "보통", "나쁨", "매우 나쁨"});
            
            MissionQuestion q2 = new MissionQuestion();
            q2.setId("2");
            q2.setQuestion("건물의 청결도는 어떤가요?");
            q2.setType("multiple_choice");
            q2.setOptions(new String[]{"매우 좋음", "좋음", "보통", "나쁨", "매우 나쁨"});
            
            mission.setQuestions(List.of(q1, q2));
            
            return ResponseEntity.ok(new ApiResponse<>(true, mission));
        } catch (Exception e) {
            log.error("현재 미션 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to get current mission: " + e.getMessage()));
        }
    }

    /**
     * 주간 미션 참여 (기존 API 호환)
     */
    @PostMapping("/participate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> participateInMission(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> answers) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String userId = extractUserIdFromToken(authHeader);
            
            log.info("🎯 주간 미션 참여 시작!");
            log.info("👤 사용자 ID: {}", userId);
            log.info("📝 답변 수: {}", answers.size());
            
            // 임시 응답 데이터 (기존 API 호환성을 위해)
            Map<String, Object> result = new HashMap<>();
            result.put("userScore", 8);
            result.put("buildingAverage", 6.5);
            result.put("neighborhoodAverage", 7.2);
            result.put("message", "미션 참여가 완료되었습니다!");
            
            log.info("✅ 주간 미션 참여 완료!");
            log.info("📊 사용자 점수: {}", result.get("userScore"));
            log.info("🏢 건물 평균: {}", result.get("buildingAverage"));
            log.info("🏘️ 동네 평균: {}", result.get("neighborhoodAverage"));
            
            return ResponseEntity.ok(new ApiResponse<>(true, result));
        } catch (Exception e) {
            log.error("❌ 주간 미션 참여 실패: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to participate in mission: " + e.getMessage()));
        }
    }

    /**
     * 새로운 미션 시스템 API - 현재 활성 미션 조회
     */
    @GetMapping("/v2/current")
    public ResponseEntity<MissionResponseDTO.ApiResponse<MissionResponseDTO.CurrentMission>> getCurrentMissionV2(
            @AuthenticationPrincipal Member member) {

        try {
            MissionResponseDTO.CurrentMission mission = missionService.getCurrentMission(member.getId());
            return ResponseEntity.ok(MissionResponseDTO.ApiResponse.success(mission));

        } catch (Exception e) {
            log.error("현재 미션 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(MissionResponseDTO.ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 새로운 미션 시스템 API - 미션 참여하기
     */
    @PostMapping("/v2/{missionId}/participate")
    public ResponseEntity<MissionResponseDTO.ApiResponse<Map<String, Object>>> participateInMissionV2(
            @PathVariable Long missionId,
            @RequestBody MissionRequestDTO.MissionParticipate request,
            @AuthenticationPrincipal Member member) {

        try {
            Long responseId = missionService.participateInMission(member.getId(), missionId, request);

            // 총점 계산
            Integer totalScore = request.getResponses().stream()
                    .mapToInt(MissionRequestDTO.MissionParticipate.Response::getScore)
                    .sum();

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("response_id", responseId);
            responseData.put("total_score", totalScore);
            responseData.put("message", "미션 참여가 완료되었습니다!");
            responseData.put("next_step", "결과 확인하기");

            return ResponseEntity.ok(MissionResponseDTO.ApiResponse.success(responseData));

        } catch (Exception e) {
            log.error("미션 참여 실패", e);
            return ResponseEntity.badRequest()
                    .body(MissionResponseDTO.ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 새로운 미션 시스템 API - 미션 결과 조회
     */
    @GetMapping("/v2/{missionId}/result")
    public ResponseEntity<MissionResponseDTO.ApiResponse<MissionResponseDTO.MissionResult>> getMissionResultV2(
            @PathVariable Long missionId,
            @AuthenticationPrincipal Member member) {

        try {
            MissionResponseDTO.MissionResult result = missionService.getMissionResult(member.getId(), missionId);
            return ResponseEntity.ok(MissionResponseDTO.ApiResponse.success(result));

        } catch (Exception e) {
            log.error("미션 결과 조회 실패", e);
            return ResponseEntity.badRequest()
                    .body(MissionResponseDTO.ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     */
    private String extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.extractUserId(token);
        }
        throw new IllegalArgumentException("Invalid or missing authorization header");
    }
}