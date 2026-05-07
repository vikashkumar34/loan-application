package com.loanapp.service;

import com.loanapp.dto.LoginRequest;
import com.loanapp.dto.RegisterRequest;
import com.loanapp.dto.AuthResponse;
import com.loanapp.entity.User;
import com.loanapp.entity.Role;
import com.loanapp.repository.UserRepository;
import com.loanapp.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) throws Exception {
        // Validate: Only new users can register
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new Exception("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email already exists");
        }

        User user = new User(
            null, // id
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getEmail(),
            request.getFullName(),
            Role.USER,
            null, // createdAt (will be set by @PrePersist)
            null  // updatedAt (will be set by @PrePersist)
        );

        userRepository.save(user);

        return new AuthResponse(
            null, // token
            user.getUsername(),
            user.getRole().toString(),
            user.getId(),
            "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) throws Exception {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("Invalid password");
        }

        // Create JWT token
        String token = jwtTokenProvider.generateToken(
            org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole())
                .build()
        );

        return new AuthResponse(
            token,
            user.getUsername(),
            user.getRole().toString(),
            user.getId(),
            "Login successful"
        );
    }

    public User getUserByUsername(String username) throws Exception {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found"));
    }
}
