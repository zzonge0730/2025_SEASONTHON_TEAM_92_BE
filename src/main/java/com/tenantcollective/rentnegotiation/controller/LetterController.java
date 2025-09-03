package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.LetterRequest;
import com.tenantcollective.rentnegotiation.model.LetterResponse;
import com.tenantcollective.rentnegotiation.service.LetterService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class LetterController {
    
    private final LetterService letterService;
    
    @Autowired
    public LetterController(LetterService letterService) {
        this.letterService = letterService;
    }
    
    @PostMapping("/letters")
    public ResponseEntity<ApiResponse<LetterResponse>> generateLetter(@Valid @RequestBody LetterRequest request) {
        try {
            LetterResponse response = letterService.generateLetter(request);
            return ResponseEntity.ok(new ApiResponse<>(true, response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to generate letter: " + e.getMessage()));
        }
    }
}