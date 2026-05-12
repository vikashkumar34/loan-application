package com.loanapp.service;

import com.loanapp.dto.Notification;
import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.LoanStatus;
import com.loanapp.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    private final AtomicLong notificationIdCounter = new AtomicLong();

    public List<Notification> getNotificationsForUser(String username, String role) {
        List<Notification> notifications = new ArrayList<>();
        
        if ("ADMIN".equals(role)) {
            List<LoanApplication> submittedApps = loanApplicationRepository.findByStatus(LoanStatus.SUBMITTED);
            for (LoanApplication app : submittedApps) {
                notifications.add(new Notification(
                    notificationIdCounter.incrementAndGet(),
                    "New loan application #" + app.getId() + " from " + app.getUser().getFullName(),
                    app.getSubmittedDate(),
                    false, // Assuming admins haven't read it yet
                    app.getId()
                ));
            }
        } else {
            List<LoanApplication> userApps = loanApplicationRepository.findByUser_Username(username);
            for (LoanApplication app : userApps) {
                if (app.getStatus() == LoanStatus.APPROVED && app.getApprovedDate() != null) {
                    notifications.add(new Notification(
                        notificationIdCounter.incrementAndGet(),
                        "Your loan application #" + app.getId() + " has been APPROVED.",
                        app.getApprovedDate(),
                        false, // This would be tracked per user in a real DB
                        app.getId()
                    ));
                }
                if (app.getStatus() == LoanStatus.DISBURSED && app.getDisbursedDate() != null) {
                     notifications.add(new Notification(
                        notificationIdCounter.incrementAndGet(),
                        "Your loan application #" + app.getId() + " has been DISBURSED.",
                        app.getDisbursedDate(),
                        true, // Let's assume this one was read
                        app.getId()
                    ));
                }
            }
        }
        
        notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return notifications;
    }

    public void markAsRead(Long notificationId) {
        // In a real implementation, this would update a 'read' flag in a database table.
        // Since the notifications are generated on-the-fly, this method is now a placeholder.
        System.out.println("Marking notification " + notificationId + " as read (placeholder).");
    }
}
