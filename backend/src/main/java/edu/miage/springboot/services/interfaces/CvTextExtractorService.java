package edu.miage.springboot.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface CvTextExtractorService {
    String extractText(MultipartFile file);
}
