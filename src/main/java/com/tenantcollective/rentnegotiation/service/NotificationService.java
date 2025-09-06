package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.Notification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NotificationService {
    
    private final Map<String, List<Notification>> notificationsByUser = new ConcurrentHashMap<>();
    private final Map<String, Notification> notificationsById = new ConcurrentHashMap<>();
    
    public Notification createNotification(String userId, String type, String title, String message) {
        Notification notification = new Notification(userId, type, title, message);
        notification.setId("notif_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        
        notificationsById.put(notification.getId(), notification);
        notificationsByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(notification);
        
        System.out.println("🔔 알림 생성: " + notification.getTitle() + " (사용자: " + userId + ")");
        return notification;
    }
    
    public Notification createNotification(String userId, String type, String title, String message, String relatedId, String actionUrl) {
        Notification notification = createNotification(userId, type, title, message);
        notification.setRelatedId(relatedId);
        notification.setActionUrl(actionUrl);
        return notification;
    }
    
    public List<Notification> getNotificationsByUser(String userId) {
        return notificationsByUser.getOrDefault(userId, new ArrayList<>())
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }
    
    public List<Notification> getUnreadNotificationsByUser(String userId) {
        return getNotificationsByUser(userId)
                .stream()
                .filter(notification -> !notification.isRead())
                .collect(Collectors.toList());
    }
    
    public int getUnreadCount(String userId) {
        return (int) getNotificationsByUser(userId)
                .stream()
                .filter(notification -> !notification.isRead())
                .count();
    }
    
    public boolean markAsRead(String notificationId) {
        Notification notification = notificationsById.get(notificationId);
        if (notification != null) {
            notification.setRead(true);
            System.out.println("✅ 알림 읽음 처리: " + notification.getTitle());
            return true;
        }
        return false;
    }
    
    public boolean markAllAsRead(String userId) {
        List<Notification> userNotifications = notificationsByUser.get(userId);
        if (userNotifications != null) {
            userNotifications.forEach(notification -> notification.setRead(true));
            System.out.println("✅ 모든 알림 읽음 처리 (사용자: " + userId + ")");
            return true;
        }
        return false;
    }
    
    public boolean deleteNotification(String notificationId) {
        Notification notification = notificationsById.remove(notificationId);
        if (notification != null) {
            List<Notification> userNotifications = notificationsByUser.get(notification.getUserId());
            if (userNotifications != null) {
                userNotifications.removeIf(n -> n.getId().equals(notificationId));
            }
            System.out.println("🗑️ 알림 삭제: " + notification.getTitle());
            return true;
        }
        return false;
    }
    
    public Optional<Notification> getNotificationById(String notificationId) {
        return Optional.ofNullable(notificationsById.get(notificationId));
    }
    
    // 특정 이벤트에 대한 알림 생성 메서드들
    public void notifyVoteCreated(String userId, String voteTitle) {
        createNotification(
            userId,
            "vote",
            "새로운 투표가 생성되었습니다",
            "새로운 투표: " + voteTitle + "에 참여해보세요!",
            null,
            "/voting"
        );
    }
    
    public void notifyDiscussionReply(String userId, String proposalTitle) {
        createNotification(
            userId,
            "discussion",
            "토론에 새로운 댓글이 달렸습니다",
            "제안서 '" + proposalTitle + "'에 새로운 댓글이 달렸습니다.",
            null,
            "/groups"
        );
    }
    
    public void notifyReportSubmitted(String userId, String buildingName) {
        createNotification(
            userId,
            "system",
            "익명 신고가 접수되었습니다",
            buildingName + "에 대한 익명 신고가 성공적으로 접수되었습니다.",
            null,
            "/reports"
        );
    }
    
    public void notifyMissionCompleted(String userId, String missionTitle) {
        createNotification(
            userId,
            "system",
            "주간 미션 완료!",
            "주간 미션 '" + missionTitle + "'을 완료하셨습니다. 결과를 확인해보세요!",
            null,
            "/missions"
        );
    }
}