package com.loanapp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "disbursements")
public class Disbursement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplication loanApplication;

    @Column(nullable = false, unique = true, length = 12)
    private String transactionReference;

    @Column(nullable = false)
    private LocalDateTime requestedDate;

    @Column(nullable = false)
    private LocalDateTime approvedDate;

    @Column(nullable = false)
    private LocalDateTime disbursedDate;

    @Column(nullable = false)
    private String disbursedByAdmin;

    @Column(length = 500)
    private String remarks;

    public Disbursement() {
    }

    public Disbursement(Long id, LoanApplication loanApplication, String transactionReference, LocalDateTime requestedDate, LocalDateTime approvedDate, LocalDateTime disbursedDate, String disbursedByAdmin, String remarks) {
        this.id = id;
        this.loanApplication = loanApplication;
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

    public LoanApplication getLoanApplication() {
        return loanApplication;
    }

    public void setLoanApplication(LoanApplication loanApplication) {
        this.loanApplication = loanApplication;
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
        return "Disbursement{" +
                "id=" + id +
                ", loanApplication=" + loanApplication +
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
        Disbursement that = (Disbursement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @PrePersist
    protected void onCreate() {
        if (requestedDate == null) {
            requestedDate = LocalDateTime.now();
        }
    }
}
