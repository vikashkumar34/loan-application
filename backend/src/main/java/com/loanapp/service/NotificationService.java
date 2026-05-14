package com.loanapp.service;

import com.loanapp.dto.NotificationResponseDto;
import com.loanapp.entity.Notification;
import com.loanapp.entity.User;
import com.loanapp.repository.NotificationRepository;
import com.loanapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void createNotification(User user, String message, Long loanApplicationId) {
        Notification notification = new Notification(user, message, loanApplicationId);
        notificationRepository.save(notification);
    }

    public NotificationResponseDto getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        
        List<NotificationResponseDto.NotificationDto> dtos = notifications.stream()
            .map(n -> new NotificationResponseDto.NotificationDto(
                n.getId(),
                n.getMessage(),
                n.getCreatedAt(),
                n.isRead(),
                n.getLoanApplicationId()
            ))
            .collect(Collectors.toList());

        return new NotificationResponseDto(dtos, unreadCount);
    }

    public void markAsRead(Long notificationId, Long userId) throws Exception {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new Exception("Notification not found"));

        // Security check to ensure user can only mark their own notifications as read
        if (!notification.getUser().getId().equals(userId)) {
            throw new Exception("Unauthorized to mark this notification as read");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
