package com.tenantcollective.rentnegotiation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

@Service
public class GeoService {

    private final RestTemplate restTemplate;

    @Value("${vworld.api.key}")
    private String apiKey;

    @Value("${vworld.api.url}")
    private String apiUrl;

    public GeoService() {
        this.restTemplate = new RestTemplate();
    }

    public String getFullAddressFromCoordinates(double longitude, double latitude) {
        System.out.println("=== VWorld API 호출 시작 ===");
        System.out.println("입력 좌표 - 경도: " + longitude + ", 위도: " + latitude);
        
        try {
            // Use Geocoder 2.0 API for better address resolution
            String url = String.format("%s?service=address&request=GetAddress&version=2.0&crs=epsg:4326&point=%f,%f&format=json&type=both&zipcode=false&simple=false&key=%s",
                    apiUrl, longitude, latitude, apiKey);

            System.out.println("VWorld API URL: " + url);
            System.out.println("API Key: " + apiKey);
            
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("VWorld API 응답 길이: " + (response != null ? response.length() : "null"));
            System.out.println("VWorld API 응답 내용: " + response);

            JSONObject jsonResponse = new JSONObject(response);
            
            // Check for error response first
            if (jsonResponse.has("response") && jsonResponse.getJSONObject("response").has("status")) {
                String status = jsonResponse.getJSONObject("response").getString("status");
                System.out.println("API 응답 상태: " + status);
                if ("ERROR".equals(status)) {
                    System.err.println("❌ VWorld API 오류 발생!");
                    System.err.println("오류 응답: " + jsonResponse.toString());
                    System.out.println("Fallback 주소로 전환: " + getFallbackAddress(longitude, latitude));
                    return getFallbackAddress(longitude, latitude);
                }
            }
            
            // Check if response has the expected structure
            if (jsonResponse.has("response") && 
                jsonResponse.getJSONObject("response").has("result")) {
                
                System.out.println("✅ API 응답 구조 정상");
                // result is a JSONArray, not JSONObject
                JSONObject responseObj = jsonResponse.getJSONObject("response");
                if (responseObj.get("result") instanceof org.json.JSONArray) {
                    org.json.JSONArray resultArray = responseObj.getJSONArray("result");
                    System.out.println("결과 배열 길이: " + resultArray.length());
                    if (resultArray.length() > 0) {
                        // Prefer road address over parcel address for better privacy
                        String roadAddress = null;
                        String parcelAddress = null;
                        
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject item = resultArray.getJSONObject(i);
                            System.out.println("Full item: " + item.toString());
                            
                            if (item.has("text") && item.has("type")) {
                                String address = item.getString("text");
                                String type = item.getString("type");
                                
                                System.out.println("Address type: " + type + ", address: " + address);
                                
                                if ("road".equals(type)) {
                                    roadAddress = address;
                                } else if ("parcel".equals(type)) {
                                    parcelAddress = address;
                                }
                            }
                        }
                        
                        // Use road address if available, otherwise use parcel address
                        String selectedAddress = roadAddress != null ? roadAddress : parcelAddress;
                        if (selectedAddress != null) {
                            System.out.println("✅ 선택된 주소: " + selectedAddress);
                            
                            // Remove building number for privacy protection
                            String sanitizedAddress = sanitizeAddress(selectedAddress);
                            System.out.println("✅ 최종 주소: " + sanitizedAddress);
                            System.out.println("=== VWorld API 호출 성공 ===");
                            return sanitizedAddress;
                        } else {
                            System.out.println("❌ 주소를 찾을 수 없음 - Fallback으로 전환");
                        }
                    }
                }
            } else {
                System.err.println("❌ 예상치 못한 API 응답 구조: " + response);
            }
        } catch (Exception e) {
            // Log the error in a real application
            System.err.println("❌ VWorld API 호출 실패: " + e.getMessage());
            System.err.println("예외 타입: " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        
        System.out.println("❌ Fallback 주소 사용: " + getFallbackAddress(longitude, latitude));
        System.out.println("=== VWorld API 호출 종료 ===");
        return getFallbackAddress(longitude, latitude);
    }
    
    /**
     * 개인정보 보호를 위해 주소에서 건물번호를 제거하고 도로명주소 위주로 정리
     */
    private String sanitizeAddress(String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }
        
        System.out.println("Sanitizing address: " + address);
        
        // Remove building numbers and detailed location info for privacy
        // e.g., "울산광역시 울주군 범서읍 구영리 산 163-13" -> "울산광역시 울주군 범서읍"
        String[] parts = address.split(" ");
        
        // Keep only up to the administrative division (동/읍/면)
        StringBuilder sanitized = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            sanitized.append(parts[i]);
            
            // Stop at the first administrative division (동/읍/면)
            if (parts[i].endsWith("동") || parts[i].endsWith("읍") || parts[i].endsWith("면")) {
                System.out.println("Stopped at administrative division: " + parts[i]);
                break;
            }
            
            if (i < parts.length - 1) {
                sanitized.append(" ");
            }
        }
        
        String result = sanitized.toString().trim();
        System.out.println("Sanitized result: " + result);
        return result;
    }
    
    public String getDongFromCoordinates(double longitude, double latitude) {
        System.out.println("🏘️ getDongFromCoordinates 호출 - 경도: " + longitude + ", 위도: " + latitude);
        String fullAddress = getFullAddressFromCoordinates(longitude, latitude);
        System.out.println("🏠 getDongFromCoordinates에서 받은 주소: " + fullAddress);
        if (fullAddress != null && !fullAddress.isEmpty()) {
            // Parse address to extract dong
            // e.g., "울산광역시 울주군 범서읍 구영리 산 163-13"
            String[] parts = fullAddress.split(" ");
            if (parts.length >= 3) {
                // Find the dong (usually the 3rd or 4th part)
                for (int i = 2; i < parts.length; i++) {
                    if (parts[i].endsWith("동") || parts[i].endsWith("읍") || parts[i].endsWith("면")) {
                        String dong = parts[i];
                        System.out.println("Extracted dong: " + dong);
                        return dong;
                    }
                }
                // Fallback: use the 3rd part if no dong/eup/myeon found
                String fallbackDong = parts[2];
                System.out.println("Using fallback dong: " + fallbackDong);
                return fallbackDong;
            }
        }
        return getFallbackDong(longitude, latitude);
    }
    
    private String getFallbackAddress(double longitude, double latitude) {
        System.out.println("🔄 Fallback 주소 생성 중...");
        System.out.println("좌표 범위 체크 - 경도: " + longitude + " (126.9~127.1), 위도: " + latitude + " (37.4~37.7)");
        
        // Fallback: return a default address based on coordinates (privacy-protected)
        if (longitude >= 126.9 && longitude <= 127.1 && latitude >= 37.4 && latitude <= 37.7) {
            System.out.println("⚠️ 서울 지역 범위에 해당 - 강남구로 설정");
            return "서울특별시 강남구"; // Default to Gangnam area (no specific building)
        }
        System.out.println("⚠️ 서울 지역 범위 밖 - 기본 메시지 반환");
        return "위치 확인 중"; // Default fallback
    }
    
    private String getFallbackDong(double longitude, double latitude) {
        // Fallback: return a default dong name based on coordinates
        // This is a simple fallback for demo purposes
        if (longitude >= 126.9 && longitude <= 127.1 && latitude >= 37.4 && latitude <= 37.7) {
            return "강남동"; // Default to Gangnam for Seoul area
        }
        return "테스트동"; // Default fallback
    }

    /**
     * 동(洞) 단위 인증 범위 검증
     * 실제 거주자만 인증할 수 있도록 동 단위로 제한
     */
    public boolean isWithinDongRange(double userLatitude, double userLongitude, String targetDong) {
        try {
            // 사용자의 동 정보 가져오기
            String userDong = getDongFromCoordinates(userLongitude, userLatitude);
            
            if (userDong == null) {
                return false;
            }
            
            // 동 이름이 일치하는지 확인 (정확성보다는 상징성에 초점)
            return userDong.equals(targetDong);
            
        } catch (Exception e) {
            System.err.println("Dong range verification failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * 동 단위 인증을 위한 허용 범위 계산 (약 500m 반경)
     */
    public boolean isWithinAcceptableRange(double lat1, double lon1, double lat2, double lon2) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= 0.5; // 500m 이내
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // km 단위
    }
}
