package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.*;
import com.tenantcollective.rentnegotiation.service.ReportSharingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class ReportSharingController {
    
    private final ReportSharingService reportSharingService;
    
    @Autowired
    public ReportSharingController(ReportSharingService reportSharingService) {
        this.reportSharingService = reportSharingService;
    }
    
    /**
     * 리포트 공유 링크 생성
     */
    @PostMapping("/share")
    public ResponseEntity<ApiResponse<SharedReport>> createSharedReport(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String title = request.get("title");
            String summary = request.get("summary");
            
            if (userId == null || title == null || summary == null) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "userId, title, summary는 필수입니다."));
            }
            
            SharedReport sharedReport = reportSharingService.createSharedReport(userId, title, summary);
            return ResponseEntity.ok(new ApiResponse<>(true, sharedReport));
            
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, "리포트 공유 링크 생성에 실패했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 공유 리포트 조회 (임대인용)
     */
    @GetMapping("/shared/{shareToken}")
    public ResponseEntity<ApiResponse<AnonymousReport>> getSharedReport(@PathVariable String shareToken) {
        try {
            AnonymousReport anonymousReport = reportSharingService.getAnonymousReport(shareToken);
            return ResponseEntity.ok(new ApiResponse<>(true, anonymousReport));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, "공유 리포트 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 사용자의 공유 리포트 목록 조회
     */
    @GetMapping("/shared/user/{userId}")
    public ResponseEntity<ApiResponse<List<SharedReport>>> getUserSharedReports(@PathVariable String userId) {
        try {
            List<SharedReport> sharedReports = reportSharingService.getUserSharedReports(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, sharedReports));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, "공유 리포트 목록 조회에 실패했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 공유 리포트 비활성화
     */
    @DeleteMapping("/shared/{userId}/{reportId}")
    public ResponseEntity<ApiResponse<String>> deactivateSharedReport(@PathVariable String userId, @PathVariable String reportId) {
        try {
            reportSharingService.deactivateSharedReport(userId, reportId);
            return ResponseEntity.ok(new ApiResponse<>(true, "공유 리포트가 비활성화되었습니다."));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, null, "공유 리포트 비활성화에 실패했습니다: " + e.getMessage()));
        }
    }
}