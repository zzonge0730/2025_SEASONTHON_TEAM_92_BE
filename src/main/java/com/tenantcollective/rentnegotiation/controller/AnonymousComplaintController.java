package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.AnonymousComplaint;
import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.service.AnonymousComplaintService;
import com.tenantcollective.rentnegotiation.service.RealtimeNotificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app", "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app"})
public class AnonymousComplaintController {
    
    private final AnonymousComplaintService complaintService;
    private final RealtimeNotificationService realtimeNotificationService;
    
    @Autowired
    public AnonymousComplaintController(AnonymousComplaintService complaintService, RealtimeNotificationService realtimeNotificationService) {
        this.complaintService = complaintService;
        this.realtimeNotificationService = realtimeNotificationService;
    }
    
    @PostMapping("/complaints/anonymous")
    public ResponseEntity<ApiResponse<String>> createAnonymousComplaint(@Valid @RequestBody AnonymousComplaint complaint) {
        try {
            AnonymousComplaint savedComplaint = complaintService.saveComplaint(complaint);
            
            // 관리자에게 실시간 알림 전송
            realtimeNotificationService.notifyAdminNewReport(savedComplaint.getBuildingName());
            
            return ResponseEntity.ok(new ApiResponse<>(true, savedComplaint.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to save anonymous complaint: " + e.getMessage()));
        }
    }
    
    @GetMapping("/complaints/anonymous")
    public ResponseEntity<ApiResponse<List<AnonymousComplaint>>> getAllAnonymousComplaints() {
        try {
            List<AnonymousComplaint> complaints = complaintService.getAllComplaints();
            return ResponseEntity.ok(new ApiResponse<>(true, complaints));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve anonymous complaints: " + e.getMessage()));
        }
    }
    
    @GetMapping("/complaints/anonymous/building/{buildingName}")
    public ResponseEntity<ApiResponse<List<AnonymousComplaint>>> getComplaintsByBuilding(@PathVariable String buildingName) {
        try {
            List<AnonymousComplaint> complaints = complaintService.getComplaintsByBuilding(buildingName);
            return ResponseEntity.ok(new ApiResponse<>(true, complaints));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve complaints for building: " + e.getMessage()));
        }
    }
    
    @GetMapping("/complaints/anonymous/neighborhood/{neighborhood}")
    public ResponseEntity<ApiResponse<List<AnonymousComplaint>>> getComplaintsByNeighborhood(@PathVariable String neighborhood) {
        try {
            List<AnonymousComplaint> complaints = complaintService.getComplaintsByNeighborhood(neighborhood);
            return ResponseEntity.ok(new ApiResponse<>(true, complaints));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve complaints for neighborhood: " + e.getMessage()));
        }
    }
    
    @GetMapping("/complaints/anonymous/{id}")
    public ResponseEntity<ApiResponse<AnonymousComplaint>> getComplaintById(@PathVariable String id) {
        try {
            return complaintService.findComplaintById(id)
                    .map(complaint -> ResponseEntity.ok(new ApiResponse<>(true, complaint)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve complaint: " + e.getMessage()));
        }
    }
    
    @PutMapping("/complaints/anonymous/{id}/verify")
    public ResponseEntity<ApiResponse<String>> verifyComplaint(@PathVariable String id, @RequestBody Map<String, Object> request) {
        try {
            Boolean verified = (Boolean) request.get("verified");
            String verifiedBy = (String) request.get("verifiedBy");
            
            if (verified == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "Verification status is required"));
            }
            
            boolean success = complaintService.updateComplaintVerification(id, verified, verifiedBy);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Complaint verification status updated"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "Complaint not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to verify complaint: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/complaints/anonymous/{id}")
    public ResponseEntity<ApiResponse<String>> deleteComplaint(@PathVariable String id) {
        try {
            complaintService.deleteComplaint(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Complaint deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to delete complaint: " + e.getMessage()));
        }
    }
}