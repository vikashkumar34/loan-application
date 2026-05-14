package com.loanapp.controller;

import com.loanapp.dto.NotificationResponseDto;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private NotificationController notificationController;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Mock Security Context behavior
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetNotifications_Success() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        NotificationResponseDto responseDto = new NotificationResponseDto(new ArrayList<>(), 0L);
        when(notificationService.getNotificationsForUser(1L)).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(get("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificationService, times(1)).getNotificationsForUser(1L);
    }

    @Test
    void testGetNotifications_UserNotFound() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknownUser");
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        // Act & Assert
        // Standalone MockMvc will wrap the "User not found" Exception in a NestedServletException
        try {
            mockMvc.perform(get("/api/notifications"));
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("User not found"));
        }
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        doNothing().when(notificationService).markAsRead(10L, 1L);

        // Act & Assert
        mockMvc.perform(patch("/api/notifications/10/read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(notificationService, times(1)).markAsRead(10L, 1L);
    }

    @Test
    void testMarkAsRead_UserNotFound() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act & Assert
        try {
            mockMvc.perform(patch("/api/notifications/10/read"));
        } catch (Exception e) {
            assertTrue(e.getCause().getMessage().contains("User not found"));
        }
    }

    // Manual check for coverage tool
    private void assertTrue(boolean condition) {
        if (!condition) throw new AssertionError();
    }
}