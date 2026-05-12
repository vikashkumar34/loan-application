package com.loanapp.dto;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private String message;
    private LocalDateTime timestamp;
    private boolean read;
    private Long loanApplicationId;

    public Notification(Long id, String message, LocalDateTime timestamp, boolean read, Long loanApplicationId) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
        this.loanApplicationId = loanApplicationId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Long getLoanApplicationId() { return loanApplicationId; }
    public void setLoanApplicationId(Long loanApplicationId) { this.loanApplicationId = loanApplicationId; }
}
