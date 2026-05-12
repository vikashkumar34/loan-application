package com.loanapp.service;

import com.loanapp.dto.KycRequest;
import com.loanapp.entity.KycDocument;
import com.loanapp.entity.User;
import com.loanapp.repository.KycDocumentRepository;
import com.loanapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class KycService {

    @Autowired
    private KycDocumentRepository kycDocumentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public void submitKyc(Long userId, KycRequest request) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found"));

        // Update user details
        user.setPanCard(request.getPanCardNumber());
        user.setBankAccountNumber(request.getAccountNumber());
        user.setMobileNumber(request.getMobileNumber());
        // Assuming pincode is part of the address
        // user.setAddress(user.getAddress().split(",")[0] + ", " + request.getPincode());

        // Store the document
        String fileName = fileStorageService.storeFile(request.getDocument());

        KycDocument kycDocument = new KycDocument();
        kycDocument.setUser(user);
        kycDocument.setDocumentType(request.getDocumentType());
        kycDocument.setFilePath(fileName);
        kycDocument.setUploadedAt(LocalDateTime.now());
        kycDocumentRepository.save(kycDocument);

        user.setKycStatus(com.loanapp.entity.KycStatus.SUBMITTED);
        userRepository.save(user);
    }

    public List<KycDocument> getKycDocuments(Long userId) {
        return kycDocumentRepository.findByUserId(userId);
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}
