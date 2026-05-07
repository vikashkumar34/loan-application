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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanService {

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Submit a new loan application
     * Acceptance Criteria: Users cannot submit a new loan if they have an existing PENDING or APPROVED application
     */
    public LoanApplicationResponse submitLoanApplication(Long userId, LoanApplicationRequest request) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        User user = userOpt.get();

        // Check if user has existing PENDING or APPROVED applications
        List<LoanStatus> blockedStatuses = Arrays.asList(LoanStatus.SUBMITTED, LoanStatus.APPROVED);
        Optional<LoanApplication> existingApp = loanApplicationRepository
            .findTopByUserAndStatusInOrderBySubmittedDateDesc(user, blockedStatuses);
        
        if (existingApp.isPresent()) {
            throw new Exception("User cannot submit a new loan while an existing PENDING or APPROVED application exists");
        }

        LoanApplication loanApp = new LoanApplication(
            null, // id
            user,
            request.getAmount(),
            request.getTermMonths(),
            request.getPurpose(),
            request.getBankAccountNumber(),
            request.getIfscCode(),
            LoanStatus.SUBMITTED,
            null, // submittedDate (will be set by @PrePersist)
            null, // approvedDate
            null, // rejectedDate
            null, // rejectionReason
            null, // disbursedDate
            null  // transactionReference
        );

        LoanApplication saved = loanApplicationRepository.save(loanApp);
        return mapToResponse(saved);
    }

    /**
     * Get all loan applications (sorted by date)
     */
    public List<LoanApplicationResponse> getAllLoanApplications() {
        List<LoanApplication> applications = loanApplicationRepository.findAllByOrderBySubmittedDateDesc();
        return applications.stream()
            .map(this::mapToResponse)
            .toList();
    }

    /**
     * Get loan applications by status (for Admin filtering)
     */
    public List<LoanApplicationResponse> getLoanApplicationsByStatus(String status) throws Exception {
        try {
            LoanStatus loanStatus = LoanStatus.valueOf(status.toUpperCase());
            List<LoanApplication> applications = loanApplicationRepository.findByStatus(loanStatus);
            return applications.stream()
                .map(this::mapToResponse)
                .toList();
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid loan status: " + status);
        }
    }

    /**
     * Get loan applications for a specific user
     */
    public List<LoanApplicationResponse> getUserLoanApplications(Long userId) throws Exception {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found");
        }

        List<LoanApplication> applications = loanApplicationRepository.findByUser(userOpt.get());
        return applications.stream()
            .map(this::mapToResponse)
            .toList();
    }

    /**
     * Get a specific loan application by ID
     */
    public LoanApplicationResponse getLoanApplicationById(Long id) throws Exception {
        LoanApplication app = loanApplicationRepository.findById(id)
            .orElseThrow(() -> new Exception("Loan application not found"));
        return mapToResponse(app);
    }

    /**
     * Update loan application status (Admin action)
     */
    public LoanApplicationResponse updateLoanApplicationStatus(Long id, StatusUpdateRequest request) throws Exception {
        LoanApplication app = loanApplicationRepository.findById(id)
            .orElseThrow(() -> new Exception("Loan application not found"));

        try {
            LoanStatus newStatus = LoanStatus.valueOf(request.getStatus().toUpperCase());

            // Validate state transitions
            if (app.getStatus() == LoanStatus.DISBURSED) {
                throw new Exception("Cannot change status of a disbursed application");
            }

            if (app.getStatus() == LoanStatus.REJECTED) {
                throw new Exception("Cannot change status of a rejected application");
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
        } catch (IllegalArgumentException e) {
            throw new Exception("Invalid loan status: " + request.getStatus());
        }
    }

    private LoanApplicationResponse mapToResponse(LoanApplication app) {
        return new LoanApplicationResponse(
            app.getId(),
            app.getUser().getId(),
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
}
