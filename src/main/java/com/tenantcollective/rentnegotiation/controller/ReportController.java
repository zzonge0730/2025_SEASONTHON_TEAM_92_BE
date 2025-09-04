package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.AdvancedReport;
import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.DataReliability;
import com.tenantcollective.rentnegotiation.model.NegotiationCard;
import com.tenantcollective.rentnegotiation.service.ReportService;
import com.tenantcollective.rentnegotiation.service.DataReliabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class ReportController {

    private final ReportService reportService;
    private final DataReliabilityService dataReliabilityService;

    @Autowired
    public ReportController(ReportService reportService, DataReliabilityService dataReliabilityService) {
        this.reportService = reportService;
        this.dataReliabilityService = dataReliabilityService;
    }

    @GetMapping("/advanced/{userId}")
    public ResponseEntity<ApiResponse<AdvancedReport>> getAdvancedReport(@PathVariable String userId) {
        try {
            AdvancedReport report = reportService.generateAdvancedReport(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to generate advanced report: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reliability/{userId}")
    public ResponseEntity<ApiResponse<DataReliability>> getDataReliability(@PathVariable String userId) {
        try {
            DataReliability reliability = dataReliabilityService.evaluateDataReliability(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, reliability));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to evaluate data reliability: " + e.getMessage()));
        }
    }
    
    @GetMapping("/negotiation-cards/{userId}")
    public ResponseEntity<ApiResponse<List<NegotiationCard>>> getNegotiationCards(@PathVariable String userId) {
        try {
            List<NegotiationCard> cards = reportService.getNegotiationCards(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, cards));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to generate negotiation cards: " + e.getMessage()));
        }
    }
    
    @GetMapping("/negotiation-scenario/{userId}")
    public ResponseEntity<ApiResponse<String>> getNegotiationScenario(@PathVariable String userId) {
        try {
            String scenario = reportService.generateNegotiationScenario(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, scenario));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to generate negotiation scenario: " + e.getMessage()));
        }
    }
    
    @GetMapping("/success-rate/{userId}")
    public ResponseEntity<ApiResponse<Double>> getNegotiationSuccessRate(@PathVariable String userId) {
        try {
            double successRate = reportService.estimateNegotiationSuccessRate(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, successRate));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to estimate success rate: " + e.getMessage()));
        }
    }
}
