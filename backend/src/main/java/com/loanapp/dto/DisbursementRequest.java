package com.loanapp.dto;

public class DisbursementRequest {
    private Long loanApplicationId;
    private String disbursedByAdmin;
    private String remarks;

    public DisbursementRequest(Long loanApplicationId, String disbursedByAdmin, String remarks) {
        this.loanApplicationId = loanApplicationId;
        this.disbursedByAdmin = disbursedByAdmin;
        this.remarks = remarks;
    }

    public Long getLoanApplicationId() {
        return loanApplicationId;
    }

    public void setLoanApplicationId(Long loanApplicationId) {
        this.loanApplicationId = loanApplicationId;
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
