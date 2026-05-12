package com.loanapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("userFullName")
    private String userFullName;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("termMonths")
    private Integer termMonths;

    @JsonProperty("purpose")
    private String purpose;

    @JsonProperty("bankAccountNumber")
    private String bankAccountNumber;

    @JsonProperty("ifscCode")
    private String ifscCode;

    @JsonProperty("status")
    private String status;

    @JsonProperty("submittedDate")
    private LocalDateTime submittedDate;

    @JsonProperty("approvedDate")
    private LocalDateTime approvedDate;

    @JsonProperty("rejectedDate")
    private LocalDateTime rejectedDate;

    @JsonProperty("rejectionReason")
    private String rejectionReason;

    @JsonProperty("disbursedDate")
    private LocalDateTime disbursedDate;

    @JsonProperty("transactionReference")
    private String transactionReference;

    public LoanApplicationResponse() {
    }

    public LoanApplicationResponse(Long id, Long userId, String userFullName, BigDecimal amount, Integer termMonths, String purpose, String bankAccountNumber, String ifscCode, String status, LocalDateTime submittedDate, LocalDateTime approvedDate, LocalDateTime rejectedDate, String rejectionReason, LocalDateTime disbursedDate, String transactionReference) {
        this.id = id;
        this.userId = userId;
        this.userFullName = userFullName;
        this.amount = amount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.bankAccountNumber = bankAccountNumber;
        this.ifscCode = ifscCode;
        this.status = status;
        this.submittedDate = submittedDate;
        this.approvedDate = approvedDate;
        this.rejectedDate = rejectedDate;
        this.rejectionReason = rejectionReason;
        this.disbursedDate = disbursedDate;
        this.transactionReference = transactionReference;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getSubmittedDate() { return submittedDate; }
    public void setSubmittedDate(LocalDateTime submittedDate) { this.submittedDate = submittedDate; }
    public LocalDateTime getApprovedDate() { return approvedDate; }
    public void setApprovedDate(LocalDateTime approvedDate) { this.approvedDate = approvedDate; }
    public LocalDateTime getRejectedDate() { return rejectedDate; }
    public void setRejectedDate(LocalDateTime rejectedDate) { this.rejectedDate = rejectedDate; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getDisbursedDate() { return disbursedDate; }
    public void setDisbursedDate(LocalDateTime disbursedDate) { this.disbursedDate = disbursedDate; }
    public String getTransactionReference() { return transactionReference; }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }
}
