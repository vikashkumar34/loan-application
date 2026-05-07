package com.loanapp.repository;

import com.loanapp.entity.Disbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisbursementRepository extends JpaRepository<Disbursement, Long> {
    Optional<Disbursement> findByTransactionReference(String transactionReference);
    Optional<Disbursement> findByLoanApplicationId(Long loanApplicationId);
}
