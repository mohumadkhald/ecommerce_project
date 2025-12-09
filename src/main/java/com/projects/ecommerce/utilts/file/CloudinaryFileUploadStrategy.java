package com.projects.ecommerce.utilts.file;

import com.projects.ecommerce.utilts.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@Profile("!dev") // This bean will only exist in 'dev' profile
public class CloudinaryFileUploadStrategy implements FileUploadStrategy {
    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public String storeFile(MultipartFile file, String folder) throws IOException {
        return cloudinaryService.uploadToFolder(file, folder);
    }
}
