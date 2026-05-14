package com.loanapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponseDto {
    private List<NotificationDto> notifications;
    private long unreadCount;

    public NotificationResponseDto(List<NotificationDto> notifications, long unreadCount) {
        this.notifications = notifications;
        this.unreadCount = unreadCount;
    }

    public List<NotificationDto> getNotifications() { return notifications; }
    public long getUnreadCount() { return unreadCount; }

    public static class NotificationDto {
        private Long id;
        private String message;
        private LocalDateTime timestamp;
        private boolean read;
        private Long loanApplicationId;

        public NotificationDto(Long id, String message, LocalDateTime timestamp, boolean read, Long loanApplicationId) {
            this.id = id;
            this.message = message;
            this.timestamp = timestamp;
            this.read = read;
            this.loanApplicationId = loanApplicationId;
        }

        public Long getId() { return id; }
        public String getMessage() { return message; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        public Long getLoanApplicationId() { return loanApplicationId; }
    }
}
