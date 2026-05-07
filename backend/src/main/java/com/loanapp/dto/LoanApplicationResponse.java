package com.loanapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoanApplicationResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("userId")
    private Long userId;

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

    public LoanApplicationResponse(Long id, Long userId, BigDecimal amount, Integer termMonths, String purpose, String bankAccountNumber, String ifscCode, String status, LocalDateTime submittedDate, LocalDateTime approvedDate, LocalDateTime rejectedDate, String rejectionReason, LocalDateTime disbursedDate, String transactionReference) {
        this.id = id;
        this.userId = userId;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(Integer termMonths) {
        this.termMonths = termMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(LocalDateTime submittedDate) {
        this.submittedDate = submittedDate;
    }

    public LocalDateTime getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDateTime approvedDate) {
        this.approvedDate = approvedDate;
    }

    public LocalDateTime getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(LocalDateTime rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getDisbursedDate() {
        return disbursedDate;
    }

    public void setDisbursedDate(LocalDateTime disbursedDate) {
        this.disbursedDate = disbursedDate;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    @Override
    public String toString() {
        return "LoanApplicationResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", amount=" + amount +
                ", termMonths=" + termMonths +
                ", purpose='" + purpose + '\'' +
                ", bankAccountNumber='" + bankAccountNumber + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                ", status='" + status + '\'' +
                ", submittedDate=" + submittedDate +
                ", approvedDate=" + approvedDate +
                ", rejectedDate=" + rejectedDate +
                ", rejectionReason='" + rejectionReason + '\'' +
                ", disbursedDate=" + disbursedDate +
                ", transactionReference='" + transactionReference + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoanApplicationResponse that = (LoanApplicationResponse) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (termMonths != null ? !termMonths.equals(that.termMonths) : that.termMonths != null) return false;
        if (purpose != null ? !purpose.equals(that.purpose) : that.purpose != null) return false;
        if (bankAccountNumber != null ? !bankAccountNumber.equals(that.bankAccountNumber) : that.bankAccountNumber != null) return false;
        if (ifscCode != null ? !ifscCode.equals(that.ifscCode) : that.ifscCode != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (submittedDate != null ? !submittedDate.equals(that.submittedDate) : that.submittedDate != null) return false;
        if (approvedDate != null ? !approvedDate.equals(that.approvedDate) : that.approvedDate != null) return false;
        if (rejectedDate != null ? !rejectedDate.equals(that.rejectedDate) : that.rejectedDate != null) return false;
        if (rejectionReason != null ? !rejectionReason.equals(that.rejectionReason) : that.rejectionReason != null) return false;
        if (disbursedDate != null ? !disbursedDate.equals(that.disbursedDate) : that.disbursedDate != null) return false;
        return transactionReference != null ? transactionReference.equals(that.transactionReference) : that.transactionReference == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (termMonths != null ? termMonths.hashCode() : 0);
        result = 31 * result + (purpose != null ? purpose.hashCode() : 0);
        result = 31 * result + (bankAccountNumber != null ? bankAccountNumber.hashCode() : 0);
        result = 31 * result + (ifscCode != null ? ifscCode.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (submittedDate != null ? submittedDate.hashCode() : 0);
        result = 31 * result + (approvedDate != null ? approvedDate.hashCode() : 0);
        result = 31 * result + (rejectedDate != null ? rejectedDate.hashCode() : 0);
        result = 31 * result + (rejectionReason != null ? rejectionReason.hashCode() : 0);
        result = 31 * result + (disbursedDate != null ? disbursedDate.hashCode() : 0);
        result = 31 * result + (transactionReference != null ? transactionReference.hashCode() : 0);
        return result;
    }
}
