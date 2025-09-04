package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.User;
import com.tenantcollective.rentnegotiation.service.GeoService;
import com.tenantcollective.rentnegotiation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app"})
public class LocationController {

    private final GeoService geoService;
    private final UserService userService;

    @Autowired
    public LocationController(GeoService geoService, UserService userService) {
        this.geoService = geoService;
        this.userService = userService;
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<User>> verifyLocation(@RequestBody Map<String, Object> payload) {
        String userId = (String) payload.get("userId");
        double latitude = (Double) payload.get("latitude");
        double longitude = (Double) payload.get("longitude");
        String buildingName = (String) payload.get("buildingName");

        String fullAddress = geoService.getFullAddressFromCoordinates(longitude, latitude);
        String dong = geoService.getDongFromCoordinates(longitude, latitude);

        if (fullAddress == null || dong == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to verify location from coordinates."));
        }

        // 동 단위 인증 범위 검증 (실제 거주자만 인증 가능)
        // TODO: 실제 운영에서는 더 정교한 검증 로직 필요
        if (!geoService.isWithinAcceptableRange(latitude, longitude, latitude, longitude)) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "위치 인증 범위를 벗어났습니다. 실제 거주지에서 인증해주세요."));
        }

        User updatedUser = userService.updateUserAddress(userId, fullAddress, dong, buildingName);
        updatedUser.setPassword(null);

        return ResponseEntity.ok(new ApiResponse<>(true, updatedUser));
    }
}
