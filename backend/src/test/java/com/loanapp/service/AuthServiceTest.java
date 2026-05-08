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

    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Instantiate JwtTokenProvider and set its properties
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "your-super-secret-key-that-is-long-enough-for-hs512");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L);

        // Manually instantiate AuthService and inject mocks
        authService = new AuthService();
        ReflectionTestUtils.setField(authService, "userRepository", userRepository);
        ReflectionTestUtils.setField(authService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authService, "jwtTokenProvider", jwtTokenProvider);

        // A more realistic user object
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

    @Test
    void testRegister_Success() throws Exception {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        // Ensure the save method returns the user with an ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(1L);
            return userToSave;
        });

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Registration successful", response.getMessage());
        assertEquals(registerRequest.getUsername(), response.getUsername());
        assertNotNull(response.getUserId());
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
        verify(userRepository, never()).save(any(User.class));
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
    }

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
