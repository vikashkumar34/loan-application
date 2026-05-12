package com.loanapp.controller;

import com.loanapp.dto.KycRequest;
import com.loanapp.entity.KycDocument;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.KycService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    @Autowired
    private KycService kycService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/submit")
    public ResponseEntity<Void> submitKyc(@ModelAttribute KycRequest request) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
        kycService.submitKyc(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/documents")
    public ResponseEntity<List<KycDocument>> getKycDocuments() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
        List<KycDocument> documents = kycService.getKycDocuments(user.getId());
        return ResponseEntity.ok(documents);
    }
}
