package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.AnonymousReport;
import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.service.AnonymousReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AnonymousReportController {
    
    private final AnonymousReportService anonymousReportService;
    
    @Autowired
    public AnonymousReportController(AnonymousReportService anonymousReportService) {
        this.anonymousReportService = anonymousReportService;
    }
    
    @PostMapping("/reports/anonymous")
    public ResponseEntity<ApiResponse<String>> createAnonymousReport(@Valid @RequestBody AnonymousReport report) {
        try {
            AnonymousReport savedReport = anonymousReportService.saveReport(report);
            return ResponseEntity.ok(new ApiResponse<>(true, savedReport.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to save anonymous report: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reports/anonymous")
    public ResponseEntity<ApiResponse<List<AnonymousReport>>> getAllAnonymousReports() {
        try {
            List<AnonymousReport> reports = anonymousReportService.getAllReports();
            return ResponseEntity.ok(new ApiResponse<>(true, reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve anonymous reports: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reports/anonymous/building/{buildingName}")
    public ResponseEntity<ApiResponse<List<AnonymousReport>>> getReportsByBuilding(@PathVariable String buildingName) {
        try {
            List<AnonymousReport> reports = anonymousReportService.getReportsByBuilding(buildingName);
            return ResponseEntity.ok(new ApiResponse<>(true, reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve reports for building: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reports/anonymous/neighborhood/{neighborhood}")
    public ResponseEntity<ApiResponse<List<AnonymousReport>>> getReportsByNeighborhood(@PathVariable String neighborhood) {
        try {
            List<AnonymousReport> reports = anonymousReportService.getReportsByNeighborhood(neighborhood);
            return ResponseEntity.ok(new ApiResponse<>(true, reports));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve reports for neighborhood: " + e.getMessage()));
        }
    }
    
    @GetMapping("/reports/anonymous/{id}")
    public ResponseEntity<ApiResponse<AnonymousReport>> getReportById(@PathVariable String id) {
        try {
            return anonymousReportService.findReportById(id)
                    .map(report -> ResponseEntity.ok(new ApiResponse<>(true, report)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve report: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/reports/anonymous/{id}")
    public ResponseEntity<ApiResponse<String>> deleteReport(@PathVariable String id) {
        try {
            anonymousReportService.deleteReport(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Report deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to delete report: " + e.getMessage()));
        }
    }
}
