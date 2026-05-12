package com.loanapp.repository;

import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.User;
import com.loanapp.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByUser(User user);
    List<LoanApplication> findByStatus(LoanStatus status);
    List<LoanApplication> findByUserAndStatus(User user, LoanStatus status);
    Optional<LoanApplication> findTopByUserAndStatusInOrderBySubmittedDateDesc(
        User user, List<LoanStatus> statuses);
    List<LoanApplication> findAllByOrderBySubmittedDateDesc();
    List<LoanApplication> findByUser_Username(String username);
}
