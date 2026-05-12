package com.loanapp.dto;

import java.util.List;

public class NotificationResponse {
    private List<Notification> notifications;
    private long unreadCount;

    public NotificationResponse(List<Notification> notifications, long unreadCount) {
        this.notifications = notifications;
        this.unreadCount = unreadCount;
    }

    // Getters and setters
    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
}
