package com.loanapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class LoanApplicationRequest {
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

    public LoanApplicationRequest() {
    }

    public LoanApplicationRequest(BigDecimal amount, Integer termMonths, String purpose, String bankAccountNumber, String ifscCode) {
        this.amount = amount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.bankAccountNumber = bankAccountNumber;
        this.ifscCode = ifscCode;
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

    @Override
    public String toString() {
        return "LoanApplicationRequest{" +
                "amount=" + amount +
                ", termMonths=" + termMonths +
                ", purpose='" + purpose + '\'' +
                ", bankAccountNumber='" + bankAccountNumber + '\'' +
                ", ifscCode='" + ifscCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoanApplicationRequest that = (LoanApplicationRequest) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (termMonths != null ? !termMonths.equals(that.termMonths) : that.termMonths != null) return false;
        if (purpose != null ? !purpose.equals(that.purpose) : that.purpose != null) return false;
        if (bankAccountNumber != null ? !bankAccountNumber.equals(that.bankAccountNumber) : that.bankAccountNumber != null) return false;
        return ifscCode != null ? ifscCode.equals(that.ifscCode) : that.ifscCode == null;
    }

    @Override
    public int hashCode() {
        int result = amount != null ? amount.hashCode() : 0;
        result = 31 * result + (termMonths != null ? termMonths.hashCode() : 0);
        result = 31 * result + (purpose != null ? purpose.hashCode() : 0);
        result = 31 * result + (bankAccountNumber != null ? bankAccountNumber.hashCode() : 0);
        result = 31 * result + (ifscCode != null ? ifscCode.hashCode() : 0);
        return result;
    }
}
