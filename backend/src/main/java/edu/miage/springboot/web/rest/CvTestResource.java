package edu.miage.springboot.web.rest;

import edu.miage.springboot.services.interfaces.CvTextExtractorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/test")
public class CvTestResource {

    private final CvTextExtractorService cvService;

    public CvTestResource(CvTextExtractorService cvService) {
        this.cvService = cvService;
    }

    @PostMapping("/cv")
    public ResponseEntity<String> testCv(
            @RequestParam("cv") MultipartFile cv
    ) {
        String extractedText = cvService.extractText(cv);
        return ResponseEntity.ok(extractedText);
    }


} 

