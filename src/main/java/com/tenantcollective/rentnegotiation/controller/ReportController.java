package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.AdvancedReport;
import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/advanced/{userId}")
    public ResponseEntity<ApiResponse<AdvancedReport>> getAdvancedReport(@PathVariable String userId) {
        try {
            AdvancedReport report = reportService.generateAdvancedReport(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, report));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to generate advanced report: " + e.getMessage()));
        }
    }
}
