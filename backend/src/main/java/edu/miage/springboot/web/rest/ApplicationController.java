package edu.miage.springboot.web.rest;

import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.web.dtos.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    /**
     * Endpoint pour postuler à une offre.
     * On utilise MultipartFile pour recevoir le fichier binaire (CV).
     */
    @PostMapping("/apply")
    public ResponseEntity<String> apply(
            @RequestParam Long jobOfferId,
            @RequestParam Long candidateId,
            @RequestParam String cvUrl,        // On attend une URL/String maintenant
            @RequestParam(required = false) String coverLetter
    ){

        try {
            applicationService.apply(jobOfferId, candidateId, cvUrl, coverLetter);
            return ResponseEntity.ok("Candidature déposée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi : " + e.getMessage());
        }
    }
    @GetMapping
    public List<ApplicationDTO> getAllApplications() {
        return applicationService.findAll();
    }
}