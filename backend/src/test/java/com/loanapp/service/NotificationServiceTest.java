package com.loanapp.service;

import com.loanapp.dto.NotificationResponseDto;
import com.loanapp.entity.Notification;
import com.loanapp.entity.User;
import com.loanapp.repository.NotificationRepository;
import com.loanapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        notification1 = new Notification(testUser, "Message 1", 100L);
        notification1.setId(10L);
        notification1.setRead(false);
        notification1.setCreatedAt(LocalDateTime.now().minusMinutes(5));

        notification2 = new Notification(testUser, "Message 2", 101L);
        notification2.setId(11L);
        notification2.setRead(true);
        notification2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateNotification_Success() {
        // Act
        notificationService.createNotification(testUser, "New Alert", 102L);

        // Assert
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetNotificationsForUser_CalculatesUnreadCorrectly() {
        // Arrange
        List<Notification> notifications = Arrays.asList(notification2, notification1);
        when(notificationRepository.findByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(notifications);

        // Act
        NotificationResponseDto response = notificationService.getNotificationsForUser(1L);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getNotifications().size());
        assertEquals(1, response.getUnreadCount()); // Only notification1 is unread
        verify(notificationRepository, times(1)).findByUser_IdOrderByCreatedAtDesc(1L);
    }

    @Test
    void testGetNotificationsForUser_EmptyList() {
        // Arrange
        when(notificationRepository.findByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());

        // Act
        NotificationResponseDto response = notificationService.getNotificationsForUser(1L);

        // Assert
        assertEquals(0, response.getNotifications().size());
        assertEquals(0, response.getUnreadCount());
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        // Arrange
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification1));

        // Act
        notificationService.markAsRead(10L, 1L);

        // Assert
        assertTrue(notification1.isRead());
        verify(notificationRepository, times(1)).save(notification1);
    }

    @Test
    void testMarkAsRead_NotificationNotFound() {
        // Arrange
        when(notificationRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () ->
                notificationService.markAsRead(99L, 1L)
        );
        assertEquals("Notification not found", exception.getMessage());
    }

    @Test
    void testMarkAsRead_UnauthorizedUser() {
        // Arrange
        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification1));

        // Act & Assert - User ID 2 trying to mark User ID 1's notification as read
        Exception exception = assertThrows(Exception.class, () ->
                notificationService.markAsRead(10L, 2L)
        );

        assertEquals("Unauthorized to mark this notification as read", exception.getMessage());
        assertFalse(notification1.isRead()); // Ensure state was not changed
        verify(notificationRepository, never()).save(any());
    }
}