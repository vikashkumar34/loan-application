package com.loanapp.controller;

import com.loanapp.dto.*;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.UserService;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Mocking Security Context
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockSecurityUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    // --- Admin Management API Tests ---

    @Test
    void testCreateUser_Success() throws Exception {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        when(userService.createUser(any())).thenReturn(testUser);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"password\":\"pass\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateUser_Failure() throws Exception {
        when(userService.createUser(any())).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void testUpdateUserByAdmin_Success() throws Exception {
        when(userService.updateUserByAdmin(eq(1L), any())).thenReturn(testUser);

        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateUserByAdmin_NotFound() throws Exception {
        when(userService.updateUserByAdmin(anyLong(), any())).thenThrow(new RuntimeException("Not Found"));

        mockMvc.perform(put("/api/admin/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_NotFound() throws Exception {
        doThrow(new RuntimeException("Error")).when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNotFound());
    }

    // --- User Profile API Tests ---

    @Test
    void testGetProfile_Success() throws Exception {
        mockSecurityUser();
        ProfileResponse response = new ProfileResponse("testuser", "Full Name", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        when(userService.getProfile(1L)).thenReturn(response);

        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void testGetProfile_Error() throws Exception {
        // Mock getCurrentUser() failure path
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("unknown");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUserProfile_Success() throws Exception {
        mockSecurityUser();
        when(userService.updateUserProfile(eq(1L), any())).thenReturn(testUser);

        mockMvc.perform(put("/api/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"new@email.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        mockSecurityUser();
        doNothing().when(userService).changePassword(eq(1L), any());

        mockMvc.perform(patch("/api/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"old\", \"newPassword\":\"new\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testChangePassword_BadRequest() throws Exception {
        mockSecurityUser();
        doThrow(new RuntimeException("Invalid pass")).when(userService).changePassword(anyLong(), any());

        mockMvc.perform(patch("/api/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}