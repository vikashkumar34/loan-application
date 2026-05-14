package com.loanapp.controller;

import com.loanapp.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.Matchers.containsString; // Required for fixing your compilation issue
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FileUploadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private FileUploadController fileUploadController;

    @BeforeEach
    void setUp() {
        // standaloneSetup is used to test the controller in isolation
        mockMvc = MockMvcBuilders.standaloneSetup(fileUploadController).build();
    }

    /**
     * Tests the file upload endpoint.
     * Covers: Successful storage, JSON response mapping, and URI generation.
     */
    @Test
    void testUploadFile_Success() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        when(fileStorageService.storeFile(any())).thenReturn("test.jpg");

        // Act & Assert
        mockMvc.perform(multipart("/api/files/upload")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("test.jpg"))
                // Fixed compilation by using containsString matcher
                .andExpect(jsonPath("$.fileDownloadUri", containsString("/api/files/test.jpg")));

        verify(fileStorageService, times(1)).storeFile(any());
    }

    /**
     * Tests the file download endpoint.
     * Covers: Path resolution, Resource creation, and proper Header setting.
     */
    @Test
    void testDownloadFile_Success() throws Exception {
        // Arrange
        String filename = "test.jpg";

        // We create a temporary physical file so the 'resource.exists()' check passes
        Path tempFile = Files.createTempFile("test", ".jpg");
        Files.write(tempFile, "fake image data".getBytes());

        when(fileStorageService.loadFileAsPath(filename)).thenReturn(tempFile);

        // Act & Assert
        mockMvc.perform(get("/api/files/" + filename))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + tempFile.getFileName() + "\""))
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));

        // Cleanup the temporary file created for the test
        Files.deleteIfExists(tempFile);
    }

    /**
     * Tests the download failure path.
     * Covers: Branch where the file path is found but the file does not exist on disk.
     */
    @Test
    void testDownloadFile_FileNotFound() throws Exception {
        // Arrange
        String filename = "nonexistent.jpg";
        // Path points to a file that definitely doesn't exist
        Path nonExistentPath = Path.of("target/nonexistent_file.jpg");

        when(fileStorageService.loadFileAsPath(filename)).thenReturn(nonExistentPath);

        // Act & Assert
        try {
            mockMvc.perform(get("/api/files/" + filename));
        } catch (Exception e) {
            // StandaloneSetup propagates the RuntimeException thrown by the controller
            // Using getCause() because MockMvc wraps exceptions in NestedServletException
            assertNotNull(e.getCause());
            assertEquals("Could not read the file!", e.getCause().getMessage());
        }
    }
}