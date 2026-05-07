package com.loanapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatusUpdateRequest {
    @JsonProperty("status")
    private String status;

    @JsonProperty("rejectionReason")
    private String rejectionReason;

    public StatusUpdateRequest() {
    }

    public StatusUpdateRequest(String status, String rejectionReason) {
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    @Override
    public String toString() {
        return "StatusUpdateRequest{" +
                "status='" + status + '\'' +
                ", rejectionReason='" + rejectionReason + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusUpdateRequest that = (StatusUpdateRequest) o;

        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        return rejectionReason != null ? rejectionReason.equals(that.rejectionReason) : that.rejectionReason == null;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (rejectionReason != null ? rejectionReason.hashCode() : 0);
        return result;
    }
}
