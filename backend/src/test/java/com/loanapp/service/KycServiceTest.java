package com.loanapp.service;

import com.loanapp.dto.KycRequest;
import com.loanapp.entity.KycDocument;
import com.loanapp.entity.KycStatus;
import com.loanapp.entity.User;
import com.loanapp.repository.KycDocumentRepository;
import com.loanapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KycServiceTest {

    @Mock
    private KycDocumentRepository kycDocumentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private KycService kycService;

    private User testUser;
    private KycRequest testRequest;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setKycStatus(KycStatus.PENDING);

        mockFile = mock(MultipartFile.class);

        testRequest = new KycRequest();
        testRequest.setPanCardNumber("ABCDE1234F");
        testRequest.setAccountNumber("1234567890");
        testRequest.setMobileNumber("9876543210");
        testRequest.setDocumentType("AADHAR");
        testRequest.setDocument(mockFile);
    }

    @Test
    void testSubmitKyc_Success() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fileStorageService.storeFile(mockFile)).thenReturn("stored_filename.pdf");

        // Act
        kycService.submitKyc(1L, testRequest);

        // Assert
        assertEquals("ABCDE1234F", testUser.getPanCard());
        assertEquals("1234567890", testUser.getBankAccountNumber());
        assertEquals(KycStatus.SUBMITTED, testUser.getKycStatus());

        // Verify repository interactions
        verify(fileStorageService, times(1)).storeFile(any());
        verify(kycDocumentRepository, times(1)).save(any(KycDocument.class));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testSubmitKyc_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            kycService.submitKyc(1L, testRequest);
        });

        assertEquals("User not found", exception.getMessage());
        verify(fileStorageService, never()).storeFile(any());
        verify(kycDocumentRepository, never()).save(any());
    }

    @Test
    void testGetKycDocuments_Success() {
        // Arrange
        KycDocument doc = new KycDocument();
        doc.setId(10L);
        when(kycDocumentRepository.findByUserId(1L)).thenReturn(Collections.singletonList(doc));

        // Act
        List<KycDocument> result = kycService.getKycDocuments(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        verify(kycDocumentRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testSubmitKyc_FileStorageFails() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(fileStorageService.storeFile(any())).thenThrow(new RuntimeException("Storage full"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            kycService.submitKyc(1L, testRequest);
        });

        // Verify that KYC status was NOT updated due to exception
        assertNotEquals(KycStatus.SUBMITTED, testUser.getKycStatus());
        verify(kycDocumentRepository, never()).save(any());
    }

    @Test
    void testGetUserRepository() {
        // Assert
        assertNotNull(kycService.getUserRepository());
        assertEquals(userRepository, kycService.getUserRepository());
    }
}
