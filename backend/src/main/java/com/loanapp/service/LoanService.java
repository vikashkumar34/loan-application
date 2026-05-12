package com.loanapp.service;

import com.loanapp.dto.LoanApplicationRequest;
import com.loanapp.dto.LoanApplicationResponse;
import com.loanapp.dto.StatusUpdateRequest;
import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.LoanStatus;
import com.loanapp.entity.User;
import com.loanapp.repository.LoanApplicationRepository;
import com.loanapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    public LoanApplicationResponse submitLoanApplication(Long userId, LoanApplicationRequest request) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));

        List<LoanStatus> blockedStatuses = Arrays.asList(LoanStatus.SUBMITTED, LoanStatus.APPROVED);
        Optional<LoanApplication> existingApp = loanApplicationRepository.findTopByUserAndStatusInOrderBySubmittedDateDesc(user, blockedStatuses);
        if (existingApp.isPresent()) {
            throw new Exception("User cannot submit a new loan while an existing application is pending or approved.");
        }

        LoanApplication loanApp = new LoanApplication();
        loanApp.setUser(user);
        loanApp.setAmount(request.getAmount());
        loanApp.setTermMonths(request.getTermMonths());
        loanApp.setPurpose(request.getPurpose());
        loanApp.setBankAccountNumber(request.getBankAccountNumber());
        loanApp.setIfscCode(request.getIfscCode());
        loanApp.setJobStatus(request.getJobStatus());
        loanApp.setAnnualIncome(request.getAnnualIncome());
        loanApp.setLoanType(request.getLoanType());
        loanApp.setCibilScore(request.getCibilScore());

        calculateInterestRate(loanApp);

        LoanApplication saved = loanApplicationRepository.save(loanApp);
        return mapToResponse(saved);
    }

    private void calculateInterestRate(LoanApplication loanApp) {
        BigDecimal baseRate = BigDecimal.ZERO;
        BigDecimal annualIncome = loanApp.getAnnualIncome();

        if ("Personal Loan".equalsIgnoreCase(loanApp.getLoanType())) {
            if (annualIncome.compareTo(new BigDecimal("5000000")) > 0) baseRate = new BigDecimal("10.0");
            else if (annualIncome.compareTo(new BigDecimal("4000000")) > 0) baseRate = new BigDecimal("11.0");
            else baseRate = new BigDecimal("12.0");
        } else if ("Home Loan".equalsIgnoreCase(loanApp.getLoanType())) {
            if (annualIncome.compareTo(new BigDecimal("5000000")) > 0) baseRate = new BigDecimal("7.0");
            else if (annualIncome.compareTo(new BigDecimal("4000000")) > 0) baseRate = new BigDecimal("7.5");
            else if (annualIncome.compareTo(new BigDecimal("3000000")) > 0) baseRate = new BigDecimal("8.0");
            else if (annualIncome.compareTo(new BigDecimal("2000000")) > 0) baseRate = new BigDecimal("8.5");
            else baseRate = new BigDecimal("9.0");
        } else if ("Education Loan".equalsIgnoreCase(loanApp.getLoanType())) {
            if (annualIncome.compareTo(new BigDecimal("5000000")) > 0) baseRate = new BigDecimal("7.0");
            else if (annualIncome.compareTo(new BigDecimal("4000000")) > 0) baseRate = new BigDecimal("7.5");
            else if (annualIncome.compareTo(new BigDecimal("3000000")) > 0) baseRate = new BigDecimal("8.0");
            else baseRate = new BigDecimal("8.5");
        }

        if (loanApp.getCibilScore() != null && loanApp.getCibilScore() <= 750) {
            baseRate = baseRate.add(BigDecimal.ONE);
        }

        loanApp.setInterestRate(baseRate);
    }

    private LoanApplicationResponse mapToResponse(LoanApplication app) {
        return new LoanApplicationResponse(
            app.getId(),
            app.getUser().getId(),
            app.getUser().getFullName(),
            app.getAmount(),
            app.getTermMonths(),
            app.getPurpose(),
            app.getBankAccountNumber(),
            app.getIfscCode(),
            app.getStatus().toString(),
            app.getSubmittedDate(),
            app.getApprovedDate(),
            app.getRejectedDate(),
            app.getRejectionReason(),
            app.getDisbursedDate(),
            app.getTransactionReference()
        );
    }
    
    public List<LoanApplicationResponse> getAllLoanApplications() {
        return loanApplicationRepository.findAllByOrderBySubmittedDateDesc().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<LoanApplicationResponse> getLoanApplicationsByStatus(String status) throws Exception {
        LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
        return loanApplicationRepository.findByStatus(loanStatus).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<LoanApplicationResponse> getUserLoanApplications(Long userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));
        return loanApplicationRepository.findByUser(user).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public LoanApplicationResponse getLoanApplicationById(Long id) throws Exception {
        LoanApplication app = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new Exception("Loan application not found"));
        return mapToResponse(app);
    }

    public LoanApplicationResponse updateLoanApplicationStatus(Long id, StatusUpdateRequest request) throws Exception {
        LoanApplication app = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new Exception("Loan application not found"));
        LoanStatus newStatus = LoanStatus.valueOf(request.getStatus().toUpperCase());

        if (app.getStatus() == LoanStatus.DISBURSED || app.getStatus() == LoanStatus.REJECTED) {
            throw new Exception("Cannot change status of a disbursed or rejected application");
        }

        if (newStatus == LoanStatus.APPROVED) {
            app.setStatus(LoanStatus.APPROVED);
            app.setApprovedDate(LocalDateTime.now());
        } else if (newStatus == LoanStatus.REJECTED) {
            app.setStatus(LoanStatus.REJECTED);
            app.setRejectedDate(LocalDateTime.now());
            app.setRejectionReason(request.getRejectionReason());
        } else {
            throw new Exception("Invalid status transition");
        }

        LoanApplication updated = loanApplicationRepository.save(app);
        return mapToResponse(updated);
    }
}
