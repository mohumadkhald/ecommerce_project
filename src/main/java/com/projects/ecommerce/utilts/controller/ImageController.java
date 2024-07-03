package com.projects.ecommerce.utilts.controller;//
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.core.io.Resource;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.nio.file.Files;
//
//@RestController
//public class ImageController {
//
//    @GetMapping("/images/{imageName}")
//    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) throws IOException {
//        Resource resource = new ClassPathResource("uploads/12/messages" + imageName);
//
//        if (!resource.exists()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        byte[] imageBytes = Files.readAllBytes(resource.getFile().toPath());
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(imageBytes);
//    }
//}
//
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    @GetMapping("/public/images/{product_title}/{imageName:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName, @PathVariable String product_title) throws IOException {
        // Define the directory where the images are stored
        String directory;

        // Check if product_title is a number
        if (product_title.matches("\\d+")) {
            directory = "uploads/users/" + product_title;
        } else {
            directory = "uploads/products/" + product_title;
        }

        // Construct the full path to the image
        String imagePath = directory + "/" + imageName;

        // Load the image from the filesystem
        Path imageFilePath = Paths.get(imagePath);
        if (!Files.exists(imageFilePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] imageBytes = Files.readAllBytes(imageFilePath);

        // Determine media type based on file extension
        MediaType mediaType = getImageMediaType(imageName);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imageBytes);
    }

    private MediaType getImageMediaType(String imageName) {
        String extension = imageName.substring(imageName.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

}
