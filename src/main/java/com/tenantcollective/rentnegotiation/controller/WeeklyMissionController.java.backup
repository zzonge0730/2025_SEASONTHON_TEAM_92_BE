package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.WeeklyMission;
import com.tenantcollective.rentnegotiation.service.WeeklyMissionService;
import com.tenantcollective.rentnegotiation.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class WeeklyMissionController {

    private final WeeklyMissionService weeklyMissionService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public WeeklyMissionController(WeeklyMissionService weeklyMissionService, JwtTokenProvider jwtTokenProvider) {
        this.weeklyMissionService = weeklyMissionService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * 현재 활성화된 주간 미션 조회
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<WeeklyMission>> getCurrentMission() {
        try {
            WeeklyMission mission = weeklyMissionService.getCurrentMission();
            return ResponseEntity.ok(new ApiResponse<>(true, mission));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to get current mission: " + e.getMessage()));
        }
    }

    /**
     * 주간 미션 참여
     */
    @PostMapping("/participate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> participateInMission(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> answers) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String userId = extractUserIdFromToken(authHeader);
            
            System.out.println("🎯 주간 미션 참여 시작!");
            System.out.println("👤 사용자 ID: " + userId);
            System.out.println("📝 답변 수: " + answers.size());
            
            Map<String, Object> result = weeklyMissionService.participateInMission(userId, answers);
            
            System.out.println("✅ 주간 미션 참여 완료!");
            System.out.println("📊 사용자 점수: " + result.get("userScore"));
            System.out.println("🏢 건물 평균: " + result.get("buildingAverage"));
            System.out.println("🏘️ 동네 평균: " + result.get("neighborhoodAverage"));
            
            return ResponseEntity.ok(new ApiResponse<>(true, result));
        } catch (Exception e) {
            System.err.println("❌ 주간 미션 참여 실패: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to participate in mission: " + e.getMessage()));
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