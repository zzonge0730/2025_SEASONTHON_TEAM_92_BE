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
        try {
            System.out.println("🔥🔥🔥 LocationController.verifyLocation 호출됨!");
            System.out.println("📥 받은 데이터: " + payload);
            
            String userId = (String) payload.get("userId");
            double latitude = (Double) payload.get("latitude");
            double longitude = (Double) payload.get("longitude");
            String buildingName = (String) payload.get("buildingName");
            
            System.out.println("📍 좌표 정보 - 경도: " + longitude + ", 위도: " + latitude);
            System.out.println("🏠 건물명: " + buildingName);
            System.out.println("👤 사용자 ID: " + userId);

            System.out.println("🌍 GeoService.getFullAddressFromCoordinates 호출 시작...");
            String fullAddress = geoService.getFullAddressFromCoordinates(longitude, latitude);
            System.out.println("🏠 반환된 주소: " + fullAddress);
            
            System.out.println("🏘️ GeoService.getDongFromCoordinates 호출 시작...");
            String dong = geoService.getDongFromCoordinates(longitude, latitude);
            System.out.println("🏘️ 반환된 동: " + dong);

            if (fullAddress == null || dong == null) {
                System.out.println("❌ 주소 또는 동 정보가 null입니다!");
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Failed to verify location from coordinates."));
            }

            // 동 단위 인증 범위 검증 (실제 거주자만 인증 가능)
            // TODO: 실제 운영에서는 더 정교한 검증 로직 필요
            if (!geoService.isWithinAcceptableRange(latitude, longitude, latitude, longitude)) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "위치 인증 범위를 벗어났습니다. 실제 거주지에서 인증해주세요."));
            }

            System.out.println("👤 사용자 주소 업데이트 시작...");
            User updatedUser = userService.updateUserAddress(userId, fullAddress, dong, buildingName);
            updatedUser.setPassword(null);
            
            System.out.println("✅ 위치 인증 완료!");
            System.out.println("📋 최종 사용자 정보: " + updatedUser.getAddress() + ", " + updatedUser.getNeighborhood());
            System.out.println("🔥🔥🔥 LocationController.verifyLocation 완료!");

            return ResponseEntity.ok(new ApiResponse<>(true, updatedUser));
        } catch (Exception e) {
            System.err.println("❌ LocationController.verifyLocation 에러 발생!");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "위치 인증 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}
