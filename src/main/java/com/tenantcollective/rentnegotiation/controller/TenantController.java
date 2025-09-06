package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.Tenant;
import com.tenantcollective.rentnegotiation.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app", "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app"})
public class TenantController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
    private final TenantService tenantService;
    
    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }
    
    @PostMapping("/tenants")
    public ResponseEntity<ApiResponse<String>> createTenant(@Valid @RequestBody Tenant tenant) {
        try {
            logger.info("Received tenant data: currentRentKrw={}, depositKrw={}, leaseEndYyyyMm={}, consentYesNo={}", 
                       tenant.getCurrentRentKrw(), tenant.getDepositKrw(), tenant.getLeaseEndYyyyMm(), tenant.getConsentYesNo());
            Tenant savedTenant = tenantService.saveTenant(tenant);
            return ResponseEntity.ok(new ApiResponse<>(true, savedTenant.getId()));
        } catch (Exception e) {
            logger.error("Failed to save tenant", e);
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to save tenant: " + e.getMessage()));
        }
    }
    
    // Debug endpoint to see raw request data
    @PostMapping("/tenants/debug")
    public ResponseEntity<ApiResponse<String>> createTenantDebug(@RequestBody String rawJson) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, "Received JSON: " + rawJson));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to process request: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tenants")
    public ResponseEntity<ApiResponse<java.util.List<Tenant>>> getAllTenants() {
        try {
            java.util.List<Tenant> tenants = tenantService.getAllTenants();
            return ResponseEntity.ok(new ApiResponse<>(true, tenants));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve tenants: " + e.getMessage()));
        }
    }
    
    @GetMapping("/tenants/{id}")
    public ResponseEntity<ApiResponse<Tenant>> getTenantById(@PathVariable String id) {
        try {
            return tenantService.findTenantById(id)
                    .map(tenant -> ResponseEntity.ok(new ApiResponse<>(true, tenant)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve tenant: " + e.getMessage()));
        }
    }
}