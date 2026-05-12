package com.loanapp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false)
    private String purpose;

    @Column(nullable = false)
    private String bankAccountNumber;

    @Column(nullable = false)
    private String ifscCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedDate;

    @Column
    private LocalDateTime approvedDate;

    @Column
    private LocalDateTime rejectedDate;

    @Column
    private String rejectionReason;

    @Column
    private LocalDateTime disbursedDate;

    @Column
    private String transactionReference;

    // New fields for enhanced application
    private String jobStatus;
    private BigDecimal annualIncome;
    private String loanType;
    private BigDecimal interestRate;
    private Integer cibilScore;

    public LoanApplication() {
    }

    // Getters and setters for new fields
    public String getJobStatus() { return jobStatus; }
    public void setJobStatus(String jobStatus) { this.jobStatus = jobStatus; }
    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }
    public String getLoanType() { return loanType; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    public Integer getCibilScore() { return cibilScore; }
    public void setCibilScore(Integer cibilScore) { this.cibilScore = cibilScore; }

    // Existing getters and setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public LoanStatus getStatus() { return status; }
    public void setStatus(LoanStatus status) { this.status = status; }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoanApplication that = (LoanApplication) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @PrePersist
    protected void onCreate() {
        submittedDate = LocalDateTime.now();
        if (status == null) {
            status = LoanStatus.SUBMITTED;
        }
    }
}
