package com.loanapp.controller;

import com.loanapp.entity.KycDocument;
import com.loanapp.service.KycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/kyc")
@PreAuthorize("hasRole('ADMIN')")
public class AdminKycController {

    @Autowired
    private KycService kycService;

    @GetMapping("/documents/{userId}")
    public ResponseEntity<List<KycDocument>> getKycDocuments(@PathVariable Long userId) {
        List<KycDocument> documents = kycService.getKycDocuments(userId);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/approve/{userId}")
    public ResponseEntity<Void> approveKyc(@PathVariable Long userId) throws Exception {
        // In a real app, you'd have more logic here
        com.loanapp.entity.User user = kycService.getUserRepository().findById(userId).orElseThrow(() -> new Exception("User not found"));
        user.setKycStatus(com.loanapp.entity.KycStatus.VERIFIED);
        kycService.getUserRepository().save(user);
        return ResponseEntity.ok().build();
    }
}
