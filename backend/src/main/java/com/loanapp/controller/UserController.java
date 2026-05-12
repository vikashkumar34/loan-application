package com.loanapp.controller;

import com.loanapp.dto.AdminCreateUserRequest;
import com.loanapp.dto.AdminUserUpdateRequest;
import com.loanapp.dto.ChangePasswordRequest;
import com.loanapp.dto.ProfileResponse;
import com.loanapp.dto.UserProfileUpdateRequest;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // Inject UserRepository

    // Helper method to get the current user
    private User getCurrentUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Use authentication.getName() which is the standard and safe way to get the username
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found with username: " + username));
    }

    // Admin User Management APIs

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody AdminCreateUserRequest request) {
        try {
            User newUser = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUserByAdmin(@PathVariable Long id, @RequestBody AdminUserUpdateRequest request) {
        try {
            User updatedUser = userService.updateUserByAdmin(id, request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // User Profile APIs

    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ProfileResponse> getProfile() {
        try {
            User currentUser = getCurrentUser();
            ProfileResponse profile = userService.getProfile(currentUser.getId());
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/user/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<User> updateUserProfile(@RequestBody UserProfileUpdateRequest request) {
        try {
            User currentUser = getCurrentUser();
            User updatedUser = userService.updateUserProfile(currentUser.getId(), request);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/user/change-password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            User currentUser = getCurrentUser();
            userService.changePassword(currentUser.getId(), request);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
