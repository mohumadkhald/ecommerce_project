package com.projects.ecommerce.utilts;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Profile("!dev")
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadToFolder(MultipartFile file, String folder) throws IOException {
        Map<String, Object> params = ObjectUtils.asMap(
                "folder", folder
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

        return uploadResult.get("secure_url").toString();
    }
}
