package edu.miage.springboot.web.rest;

import edu.miage.springboot.services.interfaces.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam("jobOfferId") Long jobOfferId,
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("cvFile") MultipartFile cvFile) {

        try {
            applicationService.apply(jobOfferId, candidateId, cvFile);
            return ResponseEntity.ok("Votre candidature a été envoyée avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'envoi : " + e.getMessage());
        }
    }
    @GetMapping
    public List<ApplicationDTO> getAllApplications() {
        return applicationService.findAll();
    }
}