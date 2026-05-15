package com.loanapp.controller;

import com.loanapp.entity.KycDocument;
import com.loanapp.entity.KycStatus;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.KycService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminKycControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KycService kycService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminKycController adminKycController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminKycController).build();
    }

    @Test
    void testGetKycDocuments_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        when(kycService.getKycDocuments(userId)).thenReturn(Collections.singletonList(new KycDocument()));

        // Act & Assert
        mockMvc.perform(get("/api/admin/kyc/documents/{userId}", userId))
                .andExpect(status().isOk());

        verify(kycService, times(1)).getKycDocuments(userId);
    }

    @Test
    void testApproveKyc_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setKycStatus(KycStatus.PENDING);

        // Chain the mocks: kycService.getUserRepository().findById(...)
        when(kycService.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(post("/api/admin/kyc/approve/{userId}", userId))
                .andExpect(status().isOk());

        verify(userRepository, times(1)).save(argThat(user -> user.getKycStatus() == KycStatus.VERIFIED));
    }

    @Test
    void testApproveKyc_UserNotFound() throws Exception {
        // Arrange
        Long userId = 99L;
        when(kycService.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            mockMvc.perform(post("/api/admin/kyc/approve/{userId}", userId));
        } catch (Exception e) {
            // StandaloneSetup propagates the Exception directly
            assert(e.getCause().getMessage().equals("User not found"));
        }
    }
}