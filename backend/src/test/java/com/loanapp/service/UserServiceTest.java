package com.loanapp.service;

import com.loanapp.dto.*;
import com.loanapp.entity.KycStatus;
import com.loanapp.entity.Role;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setFullName("John Doe");
        user.setRole(Role.USER);
        user.setKycStatus(KycStatus.PENDING);
    }

    // --- GET PROFILE TESTS ---

    @Test
    void testGetProfile_Success() throws Exception {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ProfileResponse response = userService.getProfile(1L);

        assertNotNull(response);
        assertEquals("john_doe", response.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetProfile_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.getProfile(1L));
    }

    // --- CREATE USER TESTS ---

    @Test
    void testCreateUser_Success() throws Exception {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("newuser");
        request.setEmail("new@email.com");
        request.setPassword("pass");
        request.setRole(Role.ADMIN);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@email.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.createUser(request);
        assertNotNull(result);
    }

    @Test
    void testCreateUser_DuplicateEmail() {
        AdminCreateUserRequest request = new AdminCreateUserRequest();
        request.setUsername("unique");
        request.setEmail("exists@email.com");

        when(userRepository.existsByUsername("unique")).thenReturn(false);
        when(userRepository.existsByEmail("exists@email.com")).thenReturn(true);

        Exception ex = assertThrows(Exception.class, () -> userService.createUser(request));
        assertEquals("Email already exists", ex.getMessage());
    }

    // --- UPDATE TESTS (ADMIN) ---

    @Test
    void testUpdateUserByAdmin_Success() throws Exception {
        AdminUserUpdateRequest request = new AdminUserUpdateRequest();
        request.setUsername("admin_edit");
        request.setKycStatus(KycStatus.VERIFIED);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserByAdmin(1L, request);
        assertEquals("admin_edit", result.getUsername());
        assertEquals(KycStatus.VERIFIED, result.getKycStatus());
    }

    @Test
    void testUpdateUserByAdmin_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.updateUserByAdmin(1L, new AdminUserUpdateRequest()));
    }

    // --- UPDATE TESTS (USER PROFILE) ---

    @Test
    void testUpdateUserProfile_Success() throws Exception {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setFullName("Jane Doe");
        request.setMobileNumber("1234567890");
        request.setAadharNumber("999988887777");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.updateUserProfile(1L, request);

        assertEquals("Jane Doe", result.getFullName());
        assertEquals("1234567890", result.getMobileNumber());
        assertEquals("999988887777", result.getAadharNumber());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserProfile_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.updateUserProfile(1L, new UserProfileUpdateRequest()));
    }

    // --- DELETE TESTS ---

    @Test
    void testDeleteUser_Success() throws Exception {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    // --- PASSWORD TESTS ---

    @Test
    void testChangePassword_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(Exception.class, () -> userService.changePassword(1L, new ChangePasswordRequest()));
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        List<User> list = userService.getAllUsers();
        assertEquals(1, list.size());
    }
}