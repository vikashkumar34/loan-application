package com.loanapp.controller;

import com.loanapp.dto.LoanApplicationRequest;
import com.loanapp.dto.LoanApplicationResponse;
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

    @PostMapping("/apply")
    public ResponseEntity<LoanApplicationResponse> submitLoanApplication(@RequestBody LoanApplicationRequest request) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Long userId = authService.getUserByUsername(username).getId();
        LoanApplicationResponse response = loanService.submitLoanApplication(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<LoanApplicationResponse>> getMyApplications() throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Long userId = authService.getUserByUsername(username).getId();
        List<LoanApplicationResponse> applications = loanService.getUserLoanApplications(userId);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanApplicationResponse> getLoanApplication(@PathVariable Long id) throws Exception {
        LoanApplicationResponse application = loanService.getLoanApplicationById(id);
        return ResponseEntity.ok(application);
    }
}
