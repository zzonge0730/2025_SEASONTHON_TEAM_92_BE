package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.*;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ReportSharingService {
    
    private final ReportService reportService;
    private final DataReliabilityService dataReliabilityService;
    
    // 메모리 기반 저장소 (실제 운영에서는 DB 사용)
    private final Map<String, SharedReport> sharedReports = new HashMap<>();
    private final Map<String, String> tokenToReportId = new HashMap<>();
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    @org.springframework.beans.factory.annotation.Autowired
    public ReportSharingService(ReportService reportService, DataReliabilityService dataReliabilityService) {
        this.reportService = reportService;
        this.dataReliabilityService = dataReliabilityService;
    }
    
    /**
     * 리포트 공유 링크 생성
     */
    public SharedReport createSharedReport(String userId, String title, String summary) {
        // 신뢰도 검증
        DataReliability reliability = dataReliabilityService.evaluateDataReliability(userId);
        if (!reliability.isReportEligible()) {
            throw new IllegalStateException(reliability.getReliabilityMessage());
        }
        
        // 고유한 공유 토큰 생성
        String shareToken = generateShareToken();
        
        // 리포트 URL 생성
        String reportUrl = String.format("/shared-report/%s", shareToken);
        
        // 공유 리포트 생성
        SharedReport sharedReport = new SharedReport(
            "report_" + System.currentTimeMillis(),
            userId,
            shareToken,
            reportUrl,
            title,
            summary
        );
        
        // 저장
        sharedReports.put(sharedReport.getReportId(), sharedReport);
        tokenToReportId.put(shareToken, sharedReport.getReportId());
        
        return sharedReport;
    }
    
    /**
     * 공유 토큰으로 리포트 조회
     */
    public Optional<SharedReport> getSharedReportByToken(String shareToken) {
        String reportId = tokenToReportId.get(shareToken);
        if (reportId == null) {
            return Optional.empty();
        }
        
        SharedReport sharedReport = sharedReports.get(reportId);
        if (sharedReport == null || !sharedReport.isActive()) {
            return Optional.empty();
        }
        
        // 접근 통계 업데이트
        sharedReport.setAccessCount(sharedReport.getAccessCount() + 1);
        sharedReport.setLastAccessedAt(LocalDateTime.now());
        
        return Optional.of(sharedReport);
    }
    
    /**
     * 공유 리포트의 실제 데이터 조회 (임대인용)
     */
    public AnonymousReport getAnonymousReport(String shareToken) {
        SharedReport sharedReport = getSharedReportByToken(shareToken)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 공유 링크입니다."));
        
        // 원본 리포트 데이터 조회
        AdvancedReport originalReport = reportService.generateAdvancedReport(sharedReport.getUserId());
        
        // 임대인용 익명 리포트 생성
        return createAnonymousReport(originalReport, sharedReport);
    }
    
    /**
     * 사용자의 공유 리포트 목록 조회
     */
    public java.util.List<SharedReport> getUserSharedReports(String userId) {
        return sharedReports.values().stream()
            .filter(report -> report.getUserId().equals(userId))
            .filter(SharedReport::isActive)
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 공유 리포트 비활성화
     */
    public void deactivateSharedReport(String userId, String reportId) {
        SharedReport sharedReport = sharedReports.get(reportId);
        if (sharedReport != null && sharedReport.getUserId().equals(userId)) {
            sharedReport.setActive(false);
        }
    }
    
    /**
     * 고유한 공유 토큰 생성
     */
    private String generateShareToken() {
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
    /**
     * 임대인용 익명 리포트 생성
     */
    private AnonymousReport createAnonymousReport(AdvancedReport originalReport, SharedReport sharedReport) {
        AnonymousReport anonymousReport = new AnonymousReport();
        
        // 기본 정보
        anonymousReport.setReportId(sharedReport.getReportId());
        anonymousReport.setTitle(sharedReport.getTitle());
        anonymousReport.setSummary(sharedReport.getSummary());
        anonymousReport.setCreatedAt(sharedReport.getCreatedAt());
        
        // 신뢰도 정보 (익명화)
        DataReliability reliability = originalReport.getDataReliability();
        anonymousReport.setDataReliability(reliability);
        
        // 협상 카드 정보 (익명화)
        java.util.List<NegotiationCard> negotiationCards = reportService.getNegotiationCards(originalReport.getUserProfile().getId());
        anonymousReport.setNegotiationCards(negotiationCards);
        
        // 협상 시나리오
        String scenario = reportService.generateNegotiationScenario(originalReport.getUserProfile().getId());
        anonymousReport.setNegotiationScenario(scenario);
        
        // 협상 성공 확률
        double successRate = reportService.estimateNegotiationSuccessRate(originalReport.getUserProfile().getId());
        anonymousReport.setSuccessRate(successRate);
        
        // 접근 통계
        anonymousReport.setAccessCount(sharedReport.getAccessCount());
        anonymousReport.setLastAccessedAt(sharedReport.getLastAccessedAt());
        
        return anonymousReport;
    }
}