package com.loanapp.service;

import com.loanapp.dto.AuthResponse;
import com.loanapp.dto.LoginRequest;
import com.loanapp.dto.RegisterRequest;
import com.loanapp.entity.Role;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider; // Mocked this to control token generation

    @InjectMocks
    private AuthService authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setRole(Role.USER);

        registerRequest = new RegisterRequest("testuser", "password", "test@example.com", "Test User");
        loginRequest = new LoginRequest("testuser", "password");
    }

    // --- REGISTER TESTS ---

    @Test
    void testRegister_Success() throws Exception {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Registration successful", response.getMessage());
        assertEquals("testuser", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_UsernameExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> authService.register(registerRequest));

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_EmailExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> authService.register(registerRequest));

        assertEquals("Email already exists", exception.getMessage());
    }

    // --- LOGIN TESTS ---

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(UserDetails.class))).thenReturn("mock-jwt-token");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(user.getId(), response.getUserId());

        // Verify lastLoginTimestamp was updated
        assertNotNull(user.getLastLoginTimestamp());
        verify(userRepository, times(1)).save(user);
        verify(jwtTokenProvider, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> authService.login(loginRequest));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLogin_InvalidPassword() {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(Exception.class, () -> authService.login(loginRequest));

        assertEquals("Invalid password", exception.getMessage());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    // --- UTILITY TESTS ---

    @Test
    void testGetUserByUsername_Success() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User foundUser = authService.getUserByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    void testGetUserByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> authService.getUserByUsername("nonexistent"));

        assertEquals("User not found", exception.getMessage());
    }
}