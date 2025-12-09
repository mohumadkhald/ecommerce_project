package com.projects.ecommerce.utilts.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadStrategy {
    String storeFile(MultipartFile file, String folder) throws IOException;
}
