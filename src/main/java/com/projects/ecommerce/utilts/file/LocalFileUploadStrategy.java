package com.projects.ecommerce.utilts.file;

import com.projects.ecommerce.utilts.FileStorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Profile("dev") // This bean exists in all profiles except 'dev'
public class LocalFileUploadStrategy implements FileUploadStrategy {
    private final FileStorageService fileStorageService;

    public LocalFileUploadStrategy(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public String storeFile(MultipartFile file, String folder) throws IOException {
        return fileStorageService.storeFile(file, folder);
    }
}
