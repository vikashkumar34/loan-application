package com.loanapp.service;

import com.loanapp.dto.DisbursementResponse;
import com.loanapp.entity.Disbursement;
import com.loanapp.entity.LoanApplication;
import com.loanapp.entity.LoanStatus;
import com.loanapp.repository.DisbursementRepository;
import com.loanapp.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
public class DisbursementService {

    @Autowired
    private DisbursementRepository disbursementRepository;

    @Autowired
    private LoanApplicationRepository loanApplicationRepository;

    /**
     * Disbursement Logic - Acceptance Criteria:
     * i) An application can only be DISBURSED if its current status is APPROVED
     * ii) The system must generate a unique 12-digit transactionReference upon successful disbursement
     * iii) The disbursementDate must be timestamped to the exact second of the action
     * iv) Audit details: when requested and when approved for disbursement
     */
    public DisbursementResponse disburseAmount(Long loanApplicationId, String adminUsername) throws Exception {
        LoanApplication app = loanApplicationRepository.findById(loanApplicationId)
            .orElseThrow(() -> new Exception("Loan application not found"));

        // Criteria (i): Can only disburse APPROVED applications
        if (app.getStatus() != LoanStatus.APPROVED) {
            throw new Exception("Loan application must be APPROVED before disbursement. Current status: " + app.getStatus());
        }

        // Check if already disbursed
        if (disbursementRepository.findByLoanApplicationId(loanApplicationId).isPresent()) {
            throw new Exception("Loan application has already been disbursed");
        }

        // Generate unique 12-digit transaction reference (Criteria ii)
        String transactionReference = generateUniqueTransactionReference();

        // Create disbursement record with timestamps (Criteria iii)
        LocalDateTime disbursedDateNow = LocalDateTime.now();
        Disbursement disbursement = new Disbursement(
            null, // id
            app,
            transactionReference,
            app.getSubmittedDate(),
            app.getApprovedDate(),
            disbursedDateNow,
            adminUsername,
            null // remarks
        );

        Disbursement saved = disbursementRepository.save(disbursement);

        // Update loan application status and transaction reference
        app.setStatus(LoanStatus.DISBURSED);
        app.setDisbursedDate(disbursedDateNow);
        app.setTransactionReference(transactionReference);
        loanApplicationRepository.save(app);

        return mapToResponse(saved);
    }

    /**
     * Generate a unique 12-digit transaction reference
     */
    private String generateUniqueTransactionReference() throws Exception {
        String transactionRef;
        int maxAttempts = 10;
        int attempts = 0;

        do {
            long randomNum = 100000000000L + new Random().nextLong(900000000000L);
            transactionRef = String.valueOf(randomNum);
            attempts++;

            if (attempts >= maxAttempts) {
                throw new Exception("Failed to generate unique transaction reference");
            }
        } while (disbursementRepository.findByTransactionReference(transactionRef).isPresent());

        return transactionRef;
    }

    /**
     * Get disbursement details for a loan application
     */
    public DisbursementResponse getDisbursementByLoanId(Long loanApplicationId) throws Exception {
        Disbursement disbursement = disbursementRepository.findByLoanApplicationId(loanApplicationId)
            .orElseThrow(() -> new Exception("Disbursement record not found for this loan application"));
        return mapToResponse(disbursement);
    }

    private DisbursementResponse mapToResponse(Disbursement disbursement) {
        return new DisbursementResponse(
            disbursement.getId(),
            disbursement.getLoanApplication().getId(),
            disbursement.getTransactionReference(),
            disbursement.getRequestedDate(),
            disbursement.getApprovedDate(),
            disbursement.getDisbursedDate(),
            disbursement.getDisbursedByAdmin(),
            disbursement.getRemarks()
        );
    }
}
