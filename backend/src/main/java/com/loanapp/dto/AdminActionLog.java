package com.loanapp.dto;

import java.time.LocalDateTime;

public class AdminActionLog {
    private Long loanApplicationId;
    private String userFullName;
    private String adminUsername;
    private String action;
    private LocalDateTime timestamp;

    public AdminActionLog() {
    }

    public AdminActionLog(Long loanApplicationId, String userFullName, String adminUsername, String action, LocalDateTime timestamp) {
        this.loanApplicationId = loanApplicationId;
        this.userFullName = userFullName;
        this.adminUsername = adminUsername;
        this.action = action;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getLoanApplicationId() { return loanApplicationId; }
    public void setLoanApplicationId(Long loanApplicationId) { this.loanApplicationId = loanApplicationId; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
