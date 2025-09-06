package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.auth.JwtTokenProvider;
import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.User;
import com.tenantcollective.rentnegotiation.model.UserUpdateRequest;
import com.tenantcollective.rentnegotiation.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://172.21.135.200:3000", "https://houselent.vercel.app"})
public class AuthController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> registerUser(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            registeredUser.setPassword(null); // Do not send password back
            return ResponseEntity.ok(new ApiResponse<>(true, registeredUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");

        return userService.findByEmail(email)
                .map(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        // JWT 토큰 생성
                        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole());
                        
                        System.out.println("🔑 JWT 토큰 생성 완료!");
                        System.out.println("👤 사용자: " + user.getEmail() + " (ID: " + user.getId() + ")");
                        System.out.println("🎫 토큰 길이: " + token.length() + " 문자");
                        System.out.println("🔐 토큰 미리보기: " + token.substring(0, Math.min(50, token.length())) + "...");
                        
                        // 응답 데이터 구성
                        Map<String, Object> loginResponse = new HashMap<>();
                        loginResponse.put("user", user);
                        loginResponse.put("token", token);
                        
                        return ResponseEntity.ok(new ApiResponse<>(true, loginResponse));
                    }
                    return ResponseEntity.badRequest().<ApiResponse<Map<String, Object>>>body(new ApiResponse<>(false, null, "Invalid credentials"));
                })
                .orElse(ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid credentials")));
    }
    
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<User>> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        try {
            User updatedUser = userService.updateUser(userUpdateRequest);
            updatedUser.setPassword(null); // Do not send password back
            return ResponseEntity.ok(new ApiResponse<>(true, updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String userId = jwtTokenProvider.extractUserId(token);
                
                User user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
                user.setPassword(null); // Do not send password back
                
                return ResponseEntity.ok(new ApiResponse<>(true, user));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, "Invalid token"));
        }
    }
}
