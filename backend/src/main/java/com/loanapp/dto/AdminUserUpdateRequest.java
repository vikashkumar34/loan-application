package com.loanapp.dto;

import com.loanapp.entity.Role;
import com.loanapp.entity.KycStatus;

public class AdminUserUpdateRequest {
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private KycStatus kycStatus;

    public AdminUserUpdateRequest() {
    }

    public AdminUserUpdateRequest(String username, String fullName, String email, Role role, KycStatus kycStatus) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.kycStatus = kycStatus;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public KycStatus getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }
}
