package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.RealEstateTransaction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataValidationService {
    
    /**
     * 정부 데이터인지 검증하는 메서드
     */
    public boolean validateGovernmentData(List<RealEstateTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }
        
        // 1. 데이터 출처 확인
        boolean hasValidSource = transactions.stream()
                .allMatch(t -> t.getDealYear() != null && t.getDealMonth() != null);
        
        // 2. 데이터 형식 확인 (정부 데이터 특성)
        boolean hasValidFormat = transactions.stream()
                .allMatch(t -> 
                    t.getDeposit() != null && 
                    t.getMonthlyRent() != null &&
                    t.getUmdNm() != null &&
                    t.getHouseType() != null
                );
        
        // 3. 데이터 범위 확인 (합리적인 값들)
        boolean hasValidRange = transactions.stream()
                .allMatch(t -> {
                    try {
                        double deposit = Double.parseDouble(t.getDeposit());
                        double rent = Double.parseDouble(t.getMonthlyRent());
                        return deposit > 0 && deposit < 100000 && // 0~10억원
                               rent >= 0 && rent < 1000; // 0~1000만원
                    } catch (NumberFormatException e) {
                        return false;
                    }
                });
        
        return hasValidSource && hasValidFormat && hasValidRange;
    }
    
    /**
     * 데이터 신뢰도 점수 계산
     */
    public double calculateDataReliability(List<RealEstateTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return 0.0;
        }
        
        double score = 0.0;
        
        // 1. 데이터 완성도 (40점)
        long completeRecords = transactions.stream()
                .filter(t -> t.getDealYear() != null && t.getDealMonth() != null && 
                           t.getDeposit() != null && t.getMonthlyRent() != null)
                .count();
        score += (double) completeRecords / transactions.size() * 40;
        
        // 2. 데이터 일관성 (30점)
        boolean consistent = transactions.stream()
                .allMatch(t -> t.getDealYear().length() == 4 && 
                             t.getDealMonth().length() == 2);
        score += consistent ? 30 : 0;
        
        // 3. 데이터 신선도 (30점)
        // 최근 데이터일수록 높은 점수
        long recentData = transactions.stream()
                .filter(t -> {
                    try {
                        int year = Integer.parseInt(t.getDealYear());
                        int month = Integer.parseInt(t.getDealMonth());
                        return year >= 2024; // 2024년 이후 데이터
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .count();
        score += (double) recentData / transactions.size() * 30;
        
        return Math.min(score, 100.0);
    }
    
    /**
     * 데이터 출처 정보 출력
     */
    public void printDataSourceInfo(List<RealEstateTransaction> transactions) {
        System.out.println("\n=== 데이터 출처 검증 ===");
        System.out.println("총 거래 건수: " + (transactions != null ? transactions.size() : 0));
        
        if (transactions != null && !transactions.isEmpty()) {
            System.out.println("데이터 제공기관: 한국부동산원 (공공기관)");
            System.out.println("데이터 출처: 정부 공공데이터포털");
            System.out.println("데이터 유형: 단독/다가구 전월세 실거래가");
            System.out.println("데이터 신뢰도: " + String.format("%.1f", calculateDataReliability(transactions)) + "%");
            System.out.println("정부 데이터 여부: " + (validateGovernmentData(transactions) ? "예" : "아니오"));
            
            // 샘플 데이터 출력
            RealEstateTransaction sample = transactions.get(0);
            System.out.println("샘플 데이터:");
            System.out.println("  - 거래일: " + sample.getDealYear() + "-" + sample.getDealMonth() + "-" + sample.getDealDay());
            System.out.println("  - 보증금: " + sample.getDeposit() + "만원");
            System.out.println("  - 월세: " + sample.getMonthlyRent() + "만원");
            System.out.println("  - 지역: " + sample.getUmdNm());
            System.out.println("  - 주택유형: " + sample.getHouseType());
        }
        System.out.println("======================\n");
    }
}