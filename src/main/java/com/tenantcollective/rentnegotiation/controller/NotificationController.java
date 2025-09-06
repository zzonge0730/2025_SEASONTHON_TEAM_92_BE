package com.tenantcollective.rentnegotiation.controller;

import com.tenantcollective.rentnegotiation.model.ApiResponse;
import com.tenantcollective.rentnegotiation.model.Notification;
import com.tenantcollective.rentnegotiation.service.NotificationService;
import com.tenantcollective.rentnegotiation.auth.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "https://houselent.vercel.app", "https://houselent-3srqcm2ee-woohyeok-kangs-projects.vercel.app"})
public class NotificationController {
    
    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    public NotificationController(NotificationService notificationService, JwtTokenProvider jwtTokenProvider) {
        this.notificationService = notificationService;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = extractUserIdFromToken(authHeader);
            List<Notification> notifications = notificationService.getNotificationsByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "알림 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = extractUserIdFromToken(authHeader);
            List<Notification> notifications = notificationService.getUnreadNotificationsByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "읽지 않은 알림 조회 실패: " + e.getMessage()));
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = extractUserIdFromToken(authHeader);
            int count = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "읽지 않은 알림 개수 조회 실패: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable String id) {
        try {
            boolean success = notificationService.markAsRead(id);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "알림을 읽음으로 표시했습니다"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "알림을 찾을 수 없습니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "알림 읽음 처리 실패: " + e.getMessage()));
        }
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@RequestHeader("Authorization") String authHeader) {
        try {
            String userId = extractUserIdFromToken(authHeader);
            boolean success = notificationService.markAllAsRead(userId);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "모든 알림을 읽음으로 표시했습니다"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "알림 처리에 실패했습니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "모든 알림 읽음 처리 실패: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable String id) {
        try {
            boolean success = notificationService.deleteNotification(id);
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>(true, "알림이 삭제되었습니다"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "알림을 찾을 수 없습니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "알림 삭제 실패: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<Notification>> createNotification(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String type = request.get("type");
            String title = request.get("title");
            String message = request.get("message");
            String relatedId = request.get("relatedId");
            String actionUrl = request.get("actionUrl");
            
            if (userId == null || type == null || title == null || message == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, null, "필수 필드가 누락되었습니다"));
            }
            
            Notification notification = notificationService.createNotification(userId, type, title, message, relatedId, actionUrl);
            return ResponseEntity.ok(new ApiResponse<>(true, notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, null, "알림 생성 실패: " + e.getMessage()));
        }
    }
    
    private String extractUserIdFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtTokenProvider.extractUserId(token);
        }
        throw new IllegalArgumentException("Invalid or missing authorization header");
    }
}