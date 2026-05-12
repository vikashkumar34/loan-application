package com.loanapp.controller;

import com.loanapp.dto.Notification;
import com.loanapp.dto.NotificationResponse;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<NotificationResponse> getNotifications() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));

        List<Notification> userNotifications = notificationService.getNotificationsForUser(user.getUsername(), user.getRole().toString());
        
        long unreadCount = userNotifications.stream().filter(n -> !n.isRead()).count();
        NotificationResponse response = new NotificationResponse(userNotifications, unreadCount);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
