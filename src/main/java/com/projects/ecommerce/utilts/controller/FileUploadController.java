package com.projects.ecommerce.utilts.controller;

import com.projects.ecommerce.utilts.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class FileUploadController {

    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String folder = "test"; // Specify the folder path where you want to upload the file
            String fileName = fileStorageService.storeFile(file, folder);
            return ResponseEntity.ok().body("File uploaded successfully: " + fileName);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + ex.getMessage());
        }
    }
}
