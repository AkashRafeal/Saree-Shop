package com.sareeaura.common.controller;

import com.sareeaura.common.api.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    private final Path fileStorageLocation;

    public FileUploadController() {
        Path location;
        try {
            location = Paths.get("uploads").toAbsolutePath().normalize();
            Files.createDirectories(location);
            log.info("Initialized local uploads directory at: {}", location);
        } catch (Exception ex) {
            log.warn("Could not create uploads in current dir, falling back to temp directory: {}", ex.getMessage());
            location = Paths.get(System.getProperty("java.io.tmpdir"), "uploads").toAbsolutePath().normalize();
            try {
                Files.createDirectories(location);
                log.info("Initialized fallback uploads directory at: {}", location);
            } catch (Exception ignored) {}
        }
        this.fileStorageLocation = location;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Please select a file to upload", "/api/upload"));
        }

        String originalFilename = file.getOriginalFilename();
        String extension = ".jpg";
        if (originalFilename != null && originalFilename.lastIndexOf('.') != -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
        }

        // Validate image format
        if (!extension.matches("^\\.(jpg|jpeg|png|webp|gif|jfif)$")) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Only JPG, JPEG, PNG, WEBP, and GIF images are supported", "/api/upload"));
        }

        String fileName = "saree_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(ApiResponse.success("Image uploaded successfully", fileUrl));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Could not save uploaded image: " + ex.getMessage(), "/api/upload"));
        }
    }
}
