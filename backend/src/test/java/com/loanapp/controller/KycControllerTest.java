package com.loanapp.controller;

import com.loanapp.dto.KycRequest;
import com.loanapp.entity.KycDocument;
import com.loanapp.entity.User;
import com.loanapp.repository.UserRepository;
import com.loanapp.service.KycService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class KycControllerTest {

    private MockMvc mockMvc;

    @Mock
    private KycService kycService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private KycController kycController;

    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(kycController).build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Mock static SecurityContext
        SecurityContextHolder.setContext(securityContext);
    }

    private void mockAuth() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    }

    @Test
    void testSubmitKyc_Success() throws Exception {
        // Arrange
        mockAuth();
        MockMultipartFile file = new MockMultipartFile(
                "document", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());

        doNothing().when(kycService).submitKyc(eq(1L), any(KycRequest.class));

        // Act & Assert
        // Using multipart() for @ModelAttribute with files
        mockMvc.perform(multipart("/api/kyc/submit")
                        .file(file)
                        .param("panCardNumber", "ABCDE1234F")
                        .param("documentType", "PAN_CARD"))
                .andExpect(status().isOk());

        verify(kycService, times(1)).submitKyc(eq(1L), any(KycRequest.class));
    }

    @Test
    void testSubmitKyc_UserNotFound() throws Exception {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("invalid");
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        try {
            mockMvc.perform(multipart("/api/kyc/submit"));
        } catch (Exception e) {
            // Verify the custom exception is thrown
            assert(e.getCause().getMessage().equals("User not found"));
        }
    }

    @Test
    void testGetKycDocuments_Success() throws Exception {
        // Arrange
        mockAuth();
        when(kycService.getKycDocuments(1L)).thenReturn(Collections.singletonList(new KycDocument()));

        // Act & Assert
        mockMvc.perform(get("/api/kyc/documents"))
                .andExpect(status().isOk());

        verify(kycService, times(1)).getKycDocuments(1L);
    }

    @Test
    void testGetKycDocuments_Error() throws Exception {
        // Arrange
        mockAuth();
        when(kycService.getKycDocuments(1L)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        try {
            mockMvc.perform(get("/api/kyc/documents"));
        } catch (Exception e) {
            assert(e.getCause() instanceof RuntimeException);
        }
    }
}