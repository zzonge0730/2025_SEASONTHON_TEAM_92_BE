package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class RealtimeNotificationService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    
    @Autowired
    public RealtimeNotificationService(SimpMessagingTemplate messagingTemplate, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }
    
    public void sendNotificationToUser(String userId, Notification notification) {
        // 개별 사용자에게 알림 전송
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", notification);
        System.out.println("🔔 실시간 알림 전송: " + notification.getTitle() + " -> 사용자: " + userId);
    }
    
    public void sendNotificationToAll(String message) {
        // 모든 사용자에게 브로드캐스트
        messagingTemplate.convertAndSend("/topic/notifications", message);
        System.out.println("📢 전체 알림 브로드캐스트: " + message);
    }
    
    public void notifyVoteCreated(String userId, String voteTitle) {
        Notification notification = notificationService.createNotification(
            userId,
            "vote",
            "새로운 투표가 생성되었습니다",
            "새로운 투표: " + voteTitle + "에 참여해보세요!",
            null,
            "/voting"
        );
        sendNotificationToUser(userId, notification);
    }
    
    public void notifyDiscussionReply(String userId, String proposalTitle) {
        Notification notification = notificationService.createNotification(
            userId,
            "discussion",
            "토론에 새로운 댓글이 달렸습니다",
            "제안서 '" + proposalTitle + "'에 새로운 댓글이 달렸습니다.",
            null,
            "/groups"
        );
        sendNotificationToUser(userId, notification);
    }
    
    public void notifyReportSubmitted(String userId, String buildingName) {
        Notification notification = notificationService.createNotification(
            userId,
            "system",
            "익명 신고가 접수되었습니다",
            buildingName + "에 대한 익명 신고가 성공적으로 접수되었습니다.",
            null,
            "/reports"
        );
        sendNotificationToUser(userId, notification);
    }
    
    public void notifyMissionCompleted(String userId, String missionTitle) {
        Notification notification = notificationService.createNotification(
            userId,
            "system",
            "주간 미션 완료!",
            "주간 미션 '" + missionTitle + "'을 완료하셨습니다. 결과를 확인해보세요!",
            null,
            "/missions"
        );
        sendNotificationToUser(userId, notification);
    }
    
    public void notifyAdminNewReport(String buildingName) {
        // 관리자에게 새 신고 알림
        String message = "새로운 익명 신고가 접수되었습니다: " + buildingName;
        sendNotificationToAll(message);
    }
}