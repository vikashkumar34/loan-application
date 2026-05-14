package com.loanapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        // Use ReflectionTestUtils to set the private 'fileStorageLocation' field
        ReflectionTestUtils.setField(fileStorageService, "fileStorageLocation", tempDir);
    }

    @Test
    void testStoreFile_Success() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                "text/plain",
                "Hello, World!".getBytes()
        );

        // Act
        String fileName = fileStorageService.storeFile(file);

        // Assert
        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".txt"));
        assertTrue(Files.exists(tempDir.resolve(fileName)));
        assertEquals("Hello, World!", Files.readString(tempDir.resolve(fileName)));
    }

    @Test
    void testStoreFile_InvalidPathSequence() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
                "file",
                "../invalid.txt",
                "text/plain",
                "invalid".getBytes()
        );

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(file);
        });
        assertTrue(exception.getMessage().contains("invalid path sequence"));
    }

    @Test
    void testLoadFileAsPath() {
        // Arrange
        String fileName = "test-file.txt";
        Path filePath = tempDir.resolve(fileName);
        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            fail("Failed to create test file");
        }

        // Act
        Path loadedPath = fileStorageService.loadFileAsPath(fileName);

        // Assert
        assertNotNull(loadedPath);
        assertEquals(filePath.toAbsolutePath().normalize(), loadedPath.toAbsolutePath().normalize());
    }
}
