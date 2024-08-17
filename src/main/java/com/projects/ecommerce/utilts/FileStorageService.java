package com.projects.ecommerce.utilts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private final Path fileStorageLocation;
    @Value("${image.base-url}")
    private String baseUrl;


    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }


    public String storeFile(MultipartFile file, String folder) throws IOException {
        // Normalize folder name
        String normalizedFolderName = folder.trim().replaceAll("\\s+", "_");

        // Create folder if it doesn't exist
        Path folderPath = this.fileStorageLocation.resolve(normalizedFolderName);
        if (!Files.exists(folderPath)) {
            Files.createDirectories(folderPath);
        }

        // Get the original file name
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // Extract filename without the timestamp
        String[] parts = originalFileName.split("\\s+");
        String fileNameWithoutTimestamp = parts[0]; // Assuming the filename is before the first space

        // Get the file extension from the original file name
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex >= 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        // Remove the file extension if it's duplicated
        if (fileNameWithoutTimestamp.endsWith(fileExtension)) {
            fileNameWithoutTimestamp = fileNameWithoutTimestamp.substring(0, fileNameWithoutTimestamp.length() - fileExtension.length());
        }

        // Generate a unique file name with the original file extension
        String fileName = UUID.randomUUID().toString() + "_" + fileNameWithoutTimestamp + fileExtension;

        // Resolve the file path
        Path filePath = folderPath.resolve(fileName);

        // Copy the file to the target location
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Convert the file path to a string and remove everything before 'uploads'
        String filePathString = filePath.toString();

        // Ensure 'folder' contains a '/'
        int slashIndex = folder.indexOf("/");
        String newFolder = (slashIndex >= 0) ? folder.substring(slashIndex + 1) : folder;

        // Replace spaces in newFolder with underscores
        newFolder = newFolder.replaceAll("\\s+", "_");
        log.info(newFolder);

        // Return the new URL path
//        return "https://ec2-13-247-87-159.af-south-1.compute.amazonaws.com:8443/public/images/" + newFolder + "/" + fileName;
//        return "http://localhost:8080/public/images/" + newFolder + "/" + fileName;
//        return "http://ec2-13-247-87-159.af-south-1.compute.amazonaws.com:8082/" + folder + "/" + fileName;
        return baseUrl + folder + "/" + fileName;

    }

}
