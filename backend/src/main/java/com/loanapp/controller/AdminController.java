package com.loanapp.controller;

import com.loanapp.dto.LoanApplicationResponse;
import com.loanapp.dto.StatusUpdateRequest;
import com.loanapp.dto.DisbursementResponse;
import com.loanapp.dto.ApiResponse;
import com.loanapp.service.LoanService;
import com.loanapp.service.DisbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private DisbursementService disbursementService;

    /**
     * Get all loan applications (Admin only)
     */
    @GetMapping("/loans")
    public ResponseEntity<ApiResponse> getAllLoanApplications() {
        try {
            List<LoanApplicationResponse> applications = loanService.getAllLoanApplications();
            return ResponseEntity.ok(new ApiResponse(true, "Applications retrieved", applications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Filter loan applications by status (Admin only)
     * Acceptance Criteria: Admin must be able to filter applications by status
     */
    @GetMapping("/loans/status/{status}")
    public ResponseEntity<ApiResponse> getLoanApplicationsByStatus(@PathVariable String status) {
        try {
            List<LoanApplicationResponse> applications = loanService.getLoanApplicationsByStatus(status);
            return ResponseEntity.ok(new ApiResponse(true, "Applications retrieved", applications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Update loan application status (Admin only)
     * Can approve or reject applications
     */
    @PutMapping("/loans/{id}/status")
    public ResponseEntity<ApiResponse> updateLoanApplicationStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        try {
            LoanApplicationResponse response = loanService.updateLoanApplicationStatus(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Status updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Disburse amount for an approved loan application (Admin only)
     * Acceptance Criteria for Disbursement:
     * - Only user with ADMIN role can access
     * - Application must be APPROVED
     * - Generate unique 12-digit transactionReference
     * - Timestamp disbursementDate
     * - Audit details (requestedDate and approvedDate)
     */
    @PostMapping("/loans/{id}/disburse")
    public ResponseEntity<ApiResponse> disburseAmount(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String adminUsername = auth.getName();

            DisbursementResponse response = disbursementService.disburseAmount(id, adminUsername);
            return ResponseEntity.ok(new ApiResponse(true, "Disbursement processed successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get disbursement details for a loan application (Admin only)
     */
    @GetMapping("/loans/{id}/disbursement")
    public ResponseEntity<ApiResponse> getDisbursementDetails(@PathVariable Long id) {
        try {
            DisbursementResponse response = disbursementService.getDisbursementByLoanId(id);
            return ResponseEntity.ok(new ApiResponse(true, "Disbursement details retrieved", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
