package com.loanapp.controller;

import com.loanapp.dto.NotificationResponseDto;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<NotificationResponseDto> getNotifications() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));

        NotificationResponseDto response = notificationService.getNotificationsForUser(user.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
        
        notificationService.markAsRead(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
