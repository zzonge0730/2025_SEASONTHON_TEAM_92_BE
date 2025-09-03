package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.User;
import com.tenantcollective.rentnegotiation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://172.20.196.193:3000"})
public class AuthController {
    
    private final UserService userService;
    
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/users/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser(@Valid @RequestBody User user) {
        try {
            // GPS 기반 간단 인증 - 닉네임과 위치만으로 등록
            User savedUser = userService.saveUser(user);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", savedUser.getId());
            responseData.put("nickname", savedUser.getNickname());
            responseData.put("role", savedUser.getRole());
            responseData.put("address", savedUser.getAddress());
            
            return ResponseEntity.ok(new ApiResponse<>(true, responseData));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to register user: " + e.getMessage()));
        }
    }
    
    @PostMapping("/users/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(@RequestBody Map<String, Object> loginRequest) {
        try {
            String nickname = (String) loginRequest.get("nickname");
            Double latitude = (Double) loginRequest.get("latitude");
            Double longitude = (Double) loginRequest.get("longitude");
            
            if (nickname == null || latitude == null || longitude == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "Nickname and GPS coordinates are required"));
            }
            
            // GPS 기반 간단 인증 - 닉네임과 위치로 로그인
            User user = userService.findUserByNicknameAndLocation(nickname, latitude, longitude);
            
            if (user != null) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("id", user.getId());
                responseData.put("nickname", user.getNickname());
                responseData.put("role", user.getRole());
                responseData.put("address", user.getAddress());
                
                return ResponseEntity.ok(new ApiResponse<>(true, responseData));
            }
            
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "User not found with this nickname and location"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to login: " + e.getMessage()));
        }
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserById(@PathVariable String id) {
        try {
            return userService.findUserById(id)
                    .map(user -> {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("id", user.getId());
                        userData.put("nickname", user.getNickname());
                        userData.put("role", user.getRole());
                        userData.put("email", user.getEmail());
                        return ResponseEntity.ok(new ApiResponse<>(true, userData));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "Failed to retrieve user: " + e.getMessage()));
        }
    }
}
