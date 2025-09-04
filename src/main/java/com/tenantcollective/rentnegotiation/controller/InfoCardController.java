package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.InfoCard;
import com.tenantcollective.rentnegotiation.service.InfoCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/info-cards")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class InfoCardController {

    private final InfoCardService infoCardService;

    @Autowired
    public InfoCardController(InfoCardService infoCardService) {
        this.infoCardService = infoCardService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InfoCard>>> getAllCards() {
        try {
            List<InfoCard> cards = infoCardService.getAllCards();
            return ResponseEntity.ok(new ApiResponse<>(true, cards));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to get info cards: " + e.getMessage()));
        }
    }
}
