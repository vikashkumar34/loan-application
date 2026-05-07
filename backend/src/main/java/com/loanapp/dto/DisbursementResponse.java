package com.loanapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class DisbursementResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("loanApplicationId")
    private Long loanApplicationId;

    @JsonProperty("transactionReference")
    private String transactionReference;

    @JsonProperty("requestedDate")
    private LocalDateTime requestedDate;

    @JsonProperty("approvedDate")
    private LocalDateTime approvedDate;

    @JsonProperty("disbursedDate")
    private LocalDateTime disbursedDate;

    @JsonProperty("disbursedByAdmin")
    private String disbursedByAdmin;

    @JsonProperty("remarks")
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

    @Override
    public String toString() {
        return "DisbursementResponse{" +
                "id=" + id +
                ", loanApplicationId=" + loanApplicationId +
                ", transactionReference='" + transactionReference + '\'' +
                ", requestedDate=" + requestedDate +
                ", approvedDate=" + approvedDate +
                ", disbursedDate=" + disbursedDate +
                ", disbursedByAdmin='" + disbursedByAdmin + '\'' +
                ", remarks='" + remarks + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisbursementResponse that = (DisbursementResponse) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (loanApplicationId != null ? !loanApplicationId.equals(that.loanApplicationId) : that.loanApplicationId != null) return false;
        if (transactionReference != null ? !transactionReference.equals(that.transactionReference) : that.transactionReference != null) return false;
        if (requestedDate != null ? !requestedDate.equals(that.requestedDate) : that.requestedDate != null) return false;
        if (approvedDate != null ? !approvedDate.equals(that.approvedDate) : that.approvedDate != null) return false;
        if (disbursedDate != null ? !disbursedDate.equals(that.disbursedDate) : that.disbursedDate != null) return false;
        if (disbursedByAdmin != null ? !disbursedByAdmin.equals(that.disbursedByAdmin) : that.disbursedByAdmin != null) return false;
        return remarks != null ? remarks.equals(that.remarks) : that.remarks == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (loanApplicationId != null ? loanApplicationId.hashCode() : 0);
        result = 31 * result + (transactionReference != null ? transactionReference.hashCode() : 0);
        result = 31 * result + (requestedDate != null ? requestedDate.hashCode() : 0);
        result = 31 * result + (approvedDate != null ? approvedDate.hashCode() : 0);
        result = 31 * result + (disbursedDate != null ? disbursedDate.hashCode() : 0);
        result = 31 * result + (disbursedByAdmin != null ? disbursedByAdmin.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        return result;
    }
}
