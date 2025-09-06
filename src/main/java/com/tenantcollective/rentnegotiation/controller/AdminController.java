package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.AnonymousReport;
import com.tenantcollective.rentnegotiation.model.Vote;
import com.tenantcollective.rentnegotiation.model.User;
import com.tenantcollective.rentnegotiation.service.AnonymousReportService;
import com.tenantcollective.rentnegotiation.service.VoteService;
import com.tenantcollective.rentnegotiation.service.InfoCardService;
import com.tenantcollective.rentnegotiation.service.UserService;
import com.tenantcollective.rentnegotiation.model.InfoCard;
import com.tenantcollective.rentnegotiation.auth.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://172.20.196.193:3000", "https://houselent.vercel.app", "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app"})
public class AdminController {
    
    private final AnonymousReportService anonymousReportService;
    private final VoteService voteService;
    private final InfoCardService infoCardService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    public AdminController(AnonymousReportService anonymousReportService, VoteService voteService, InfoCardService infoCardService, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.anonymousReportService = anonymousReportService;
        this.voteService = voteService;
        this.infoCardService = infoCardService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> adminLogin(@RequestBody Map<String, String> loginRequest) {
        try {
            String adminId = loginRequest.get("adminId");
            String password = loginRequest.get("password");
            
            // 간단한 관리자 인증 (실제로는 보안 강화 필요)
            if ("admin".equals(adminId) && "admin123".equals(password)) {
                // 관리자 계정이 없으면 생성
                User adminUser = userService.findByEmail("admin@system.com").orElse(null);
                if (adminUser == null) {
                    adminUser = new User();
                    adminUser.setId("admin_001");
                    adminUser.setEmail("admin@system.com");
                    adminUser.setNickname("관리자");
                    adminUser.setRole("admin");
                    adminUser.setAddress("시스템 관리자");
                    adminUser.setBuildingName("시스템");
                    adminUser.setNeighborhood("시스템");
                    adminUser.setProfileCompleted(true);
                    adminUser.setActive(true);
                    userService.saveUser(adminUser);
                }
                
                // JWT 토큰 생성
                String token = jwtTokenProvider.createToken(adminUser.getId(), adminUser.getEmail(), adminUser.getRole());
                
                System.out.println("🔑 관리자 JWT 토큰 생성 완료!");
                System.out.println("👤 관리자: " + adminUser.getEmail() + " (ID: " + adminUser.getId() + ")");
                System.out.println("🎫 토큰 길이: " + token.length() + " 문자");
                
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("user", adminUser);
                responseData.put("token", token);
                
                return ResponseEntity.ok(new ApiResponse<>(true, responseData));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "관리자 인증에 실패했습니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "관리자 로그인 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<AnonymousReport>>> getAllReports() {
        try {
            List<AnonymousReport> reports = anonymousReportService.getAllReports();
            return ResponseEntity.ok(new ApiResponse<>(true, reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "신고 목록 조회 실패: " + e.getMessage()));
        }
    }
    
    @PutMapping("/reports/{reportId}/verify")
    public ResponseEntity<ApiResponse<String>> verifyReport(@PathVariable String reportId, @RequestBody Map<String, Boolean> request) {
        try {
            Boolean verified = request.get("verified");
            if (verified == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "검증 상태가 필요합니다"));
            }
            
            boolean success = anonymousReportService.updateReportVerification(reportId, verified);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "신고 검증 상태가 업데이트되었습니다"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "신고를 찾을 수 없습니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "신고 검증 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/votes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllVotes() {
        try {
            // 임시 투표 데이터 (실제로는 데이터베이스에서 조회)
            List<Map<String, Object>> votes = new ArrayList<>();
            
            // 예시 투표 데이터
            Map<String, Object> vote1 = new HashMap<>();
            vote1.put("id", "vote_001");
            vote1.put("title", "월세 인상 제한에 대한 의견");
            vote1.put("description", "올해 월세 인상을 3% 이하로 제한하는 것에 대한 의견을 듣고자 합니다.");
            vote1.put("options", List.of("찬성", "반대", "보류"));
            vote1.put("createdAt", java.time.LocalDateTime.now().minusDays(1).toString());
            vote1.put("deadline", java.time.LocalDateTime.now().plusDays(6).toString());
            vote1.put("status", "active");
            votes.add(vote1);
            
            return ResponseEntity.ok(new ApiResponse<>(true, votes));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "투표 목록 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/votes/{voteId}/results")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVoteResults(@PathVariable String voteId) {
        try {
            // 임시 투표 결과 데이터 (실제로는 데이터베이스에서 조회)
            Map<String, Object> results = new HashMap<>();
            results.put("voteId", voteId);
            results.put("totalVotes", 15);
            results.put("results", Map.of(
                "찬성", 8,
                "반대", 5,
                "보류", 2
            ));
            results.put("percentages", Map.of(
                "찬성", 53.3,
                "반대", 33.3,
                "보류", 13.4
            ));
            results.put("lastUpdated", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(new ApiResponse<>(true, results));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "투표 결과 조회 실패: " + e.getMessage()));
        }
    }
    
    @PostMapping("/votes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createVote(@RequestBody Map<String, Object> voteRequest) {
        try {
            String title = (String) voteRequest.get("title");
            String description = (String) voteRequest.get("description");
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) voteRequest.get("options");
            String deadlineStr = (String) voteRequest.get("deadline");
            
            if (title == null || description == null || options == null || options.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "투표 정보가 부족합니다"));
            }
            
            // 기한 처리 (기본값: 7일 후)
            java.time.LocalDateTime deadline;
            if (deadlineStr != null && !deadlineStr.isEmpty()) {
                try {
                    deadline = java.time.LocalDateTime.parse(deadlineStr);
                } catch (Exception e) {
                    return ResponseEntity.badRequest()
                            .body(new ApiResponse<>(false, null, "기한 형식이 올바르지 않습니다 (YYYY-MM-DDTHH:mm)"));
                }
            } else {
                deadline = java.time.LocalDateTime.now().plusDays(7); // 기본 7일
            }
            
            // 투표 템플릿 정보를 Map으로 반환
            Map<String, Object> voteTemplate = new HashMap<>();
            voteTemplate.put("id", "vote_" + System.currentTimeMillis());
            voteTemplate.put("title", title);
            voteTemplate.put("description", description);
            voteTemplate.put("options", options);
            voteTemplate.put("createdAt", java.time.LocalDateTime.now().toString());
            voteTemplate.put("deadline", deadline.toString());
            voteTemplate.put("status", "active");
            
            return ResponseEntity.ok(new ApiResponse<>(true, voteTemplate));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "투표 생성 실패: " + e.getMessage()));
        }
    }

    @PutMapping("/info-cards/{id}")
    public ResponseEntity<ApiResponse<InfoCard>> updateInfoCard(@PathVariable String id, @RequestBody InfoCard infoCard) {
        return ResponseEntity.ok(new ApiResponse<>(true, infoCardService.updateCard(id, infoCard)));
    }

    @DeleteMapping("/info-cards/{id}")
    public ResponseEntity<ApiResponse<String>> deleteInfoCard(@PathVariable String id) {
        infoCardService.deleteCard(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Info card deleted successfully"));
    }
}