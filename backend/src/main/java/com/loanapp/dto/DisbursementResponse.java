package com.loanapp.dto;

import java.time.LocalDateTime;

public class DisbursementResponse {
    private Long id;
    private Long loanApplicationId;
    private String transactionReference;
    private LocalDateTime requestedDate;
    private LocalDateTime approvedDate;
    private LocalDateTime disbursedDate;
    private String disbursedByAdmin;
    private String remarks;

    public DisbursementResponse() {
    }

    public DisbursementResponse(Long id, Long loanApplicationId, String transactionReference, LocalDateTime requestedDate, LocalDateTime approvedDate, LocalDateTime disbursedDate, String disbursedByAdmin, String remarks) {
        this.id = id;
        this.loanApplicationId = loanApplicationId;
        this.transactionReference = transactionReference;
        this.requestedDate = requestedDate;
        this.approvedDate = approvedDate;
        this.disbursedDate = disbursedDate;
        this.disbursedByAdmin = disbursedByAdmin;
        this.remarks = remarks;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public LocalDateTime getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDateTime approvedDate) {
        this.approvedDate = approvedDate;
    }

    public LocalDateTime getDisbursedDate() {
        return disbursedDate;
    }

    public void setDisbursedDate(LocalDateTime disbursedDate) {
        this.disbursedDate = disbursedDate;
    }

    public String getDisbursedByAdmin() {
        return disbursedByAdmin;
    }

    public void setDisbursedByAdmin(String disbursedByAdmin) {
        this.disbursedByAdmin = disbursedByAdmin;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
