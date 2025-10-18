package com.buberdinner.dinnerservice.infrastructure.client;



import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageService {

    private static final String UPLOAD_DIR = "uploads/";

    public String storeImage(MultipartFile file) {
        try {
            // Crée le dossier s'il n'existe pas
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + filename);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + filename; // Ceci sera accessible via WebConfig
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement de l'image", e);
        }
    }
}

