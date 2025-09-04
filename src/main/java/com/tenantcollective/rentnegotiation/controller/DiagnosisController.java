package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.DiagnosisResponse;
import com.tenantcollective.rentnegotiation.model.DiagnosisStats;
import com.tenantcollective.rentnegotiation.service.DiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @Autowired
    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DiagnosisResponse>> submitDiagnosis(@RequestBody DiagnosisResponse response) {
        try {
            DiagnosisResponse savedResponse = diagnosisService.saveResponse(response);
            return ResponseEntity.ok(new ApiResponse<>(true, savedResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to submit diagnosis: " + e.getMessage()));
        }
    }

    // A better approach for multiple submissions
    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<String>> submitBulkDiagnosis(@RequestBody List<DiagnosisResponse> responses) {
        try {
            for (DiagnosisResponse response : responses) {
                diagnosisService.saveResponse(response);
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Successfully submitted " + responses.size() + " responses."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to submit bulk diagnosis: " + e.getMessage()));
        }
    }


    @GetMapping("/comparison/{userId}")
    public ResponseEntity<ApiResponse<DiagnosisStats>> getComparison(@PathVariable String userId) {
        try {
            DiagnosisStats stats = diagnosisService.getComparisonStats(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, stats));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to get comparison stats: " + e.getMessage()));
        }
    }
}
