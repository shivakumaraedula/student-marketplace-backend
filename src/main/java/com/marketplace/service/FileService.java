package com.marketplace.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {

    @Value("${app.upload.dir:uploads/images}")
    private String uploadDir;

    public String saveBase64Image(String base64Content) throws IOException {
        String base64Image = base64Content;
        if (base64Content.contains(",")) {
            base64Image = base64Content.split(",")[1];
        }

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        String filename = UUID.randomUUID().toString() + ".jpg";
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(imageBytes);
        }

        return filename;
    }

    public byte[] getImage(String filename) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        return Files.readAllBytes(filePath);
    }
}
