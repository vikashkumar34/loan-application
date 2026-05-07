package com.loanapp.controller;

import com.loanapp.dto.LoanApplicationRequest;
import com.loanapp.dto.LoanApplicationResponse;
import com.loanapp.dto.ApiResponse;
import com.loanapp.entity.User;
import com.loanapp.service.AuthService;
import com.loanapp.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private AuthService authService;

    /**
     * Submit a new loan application
     * Authenticated users only
     */
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse> submitLoanApplication(@RequestBody LoanApplicationRequest request) {
        try {
            // Get authenticated user ID from security context
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Long userId = authService.getUserByUsername(username).getId();

            LoanApplicationResponse response = loanService.submitLoanApplication(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Loan application submitted successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get all loan applications for the current user
     */
    @GetMapping("/my-applications")
    public ResponseEntity<ApiResponse> getMyApplications() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Long userId = authService.getUserByUsername(username).getId();

            List<LoanApplicationResponse> applications = loanService.getUserLoanApplications(userId);
            return ResponseEntity.ok(new ApiResponse(true, "Applications retrieved", applications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Get a specific loan application by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getLoanApplication(@PathVariable Long id) {
        try {
            LoanApplicationResponse application = loanService.getLoanApplicationById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Application retrieved", application));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
