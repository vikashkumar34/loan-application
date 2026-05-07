package com.loanapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    @JsonProperty("token")
    private String token;

    @JsonProperty("username")
    private String username;

    @JsonProperty("role")
    private String role;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("message")
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String role, Long userId, String message) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthResponse that = (AuthResponse) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (role != null ? !role.equals(that.role) : that.role != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}
