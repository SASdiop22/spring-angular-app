package edu.miage.springboot.web.rest.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import edu.miage.springboot.web.dtos.offers.ApplicationStatusUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CandidatRepository candidatRepository;

    private static final String UPLOAD_DIR = "backend/uploads/cvs/";

    /**
     * Endpoint pour uploader les fichiers (CV + LM) et créer une candidature
     * POST /api/applications/apply-with-files
     */
    @PostMapping("/apply-with-files")
    @PreAuthorize("hasAnyAuthority('ROLE_CANDIDAT', 'ROLE_ADMIN')")
    public ResponseEntity<?> applyWithFiles(
            @RequestParam Long jobOfferId,
            @RequestParam Long candidateId,
            @RequestParam("cvFile") MultipartFile cvFile,
            @RequestParam(value = "coverLetterFile", required = false) MultipartFile coverLetterFile
    ) {
        try {
            // Vérifier que le candidat existe
            CandidatEntity candidate = candidatRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

            if (candidate.isArchived()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Profil archivé"));
            }

            // Vérifier que le candidat n'a pas déjà postulé sur cette offre
            boolean alreadyApplied = applicationService.findAll().stream()
                    .anyMatch(app -> app.getJobOfferId() == jobOfferId && app.getCandidateId() == candidateId);

            if (alreadyApplied) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Vous avez déjà postulé sur cette offre"));
            }

            // Valider le CV
            if (cvFile == null || cvFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Le CV est requis"));
            }

            // Sauvegarder le CV
            String cvUrl = saveFile(cvFile);

            // Valider et sauvegarder la lettre de motivation (optionnelle)
            String coverLetterUrl = null;
            if (coverLetterFile != null && !coverLetterFile.isEmpty()) {
                coverLetterUrl = saveFile(coverLetterFile);
            }

            // Créer la candidature avec les URLs des fichiers sauvegardés
            ApplicationDTO newApplication = applicationService.apply(
                    jobOfferId,
                    candidateId,
                    cvUrl,
                    coverLetterUrl
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(newApplication);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors du téléchargement des fichiers: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur: " + e.getMessage()));
        }
    }

    /**
     * Sauvegarde un fichier et retourne le nom unique
     */
    private String saveFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;

        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());

        System.out.println("✅ Fichier sauvegardé: " + filePath.getFileName());
        return uniqueFilename;
    }

    /**
     * Ancien endpoint pour compatibilité (paramètres URL simples)
     */
    @PostMapping("/apply")
    @PreAuthorize("hasAnyAuthority('ROLE_CANDIDAT', 'ROLE_ADMIN')")
    public ResponseEntity<ApplicationDTO> apply(
            @RequestParam Long jobOfferId,
            @RequestParam Long candidateId,
            @RequestParam String cvUrl,
            @RequestParam(required = false) String coverLetter
    ){
        try {
            CandidatEntity candidate = candidatRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

            if (candidate.isArchived()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }

            // Vérifier que le candidat n'a pas déjà postulé sur cette offre
            boolean alreadyApplied = applicationService.findAll().stream()
                    .anyMatch(app -> app.getJobOfferId() == jobOfferId && app.getCandidateId() == candidateId);

            if (alreadyApplied) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(null);
            }

            ApplicationDTO newApplication = applicationService.apply(jobOfferId, candidateId, cvUrl, coverLetter);
            return ResponseEntity.status(HttpStatus.CREATED).body(newApplication);
        }catch (Exception e) {
            System.err.println("Erreur lors de la candidature: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN') or (hasAuthority('ROLE_CANDIDAT') and @securityService.isApplicationOwner(#id))")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        try {
            ApplicationDTO application = applicationService.findById(id);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN')")
    public List<ApplicationDTO> getAllApplications() {
        return applicationService.findAll();
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or ((hasAuthority('ROLE_CANDIDAT') and @securityService.isApplicationOwner(#candidateId)))")
    public List<ApplicationDTO> getApplicationsByCandidate(@PathVariable Long candidateId) {
        return applicationService.findByCandidateId(candidateId);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN')")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            String statusStr = body.get("status");
            ApplicationStatusEnum status = ApplicationStatusEnum.valueOf(statusStr);
            ApplicationDTO application = applicationService.updateStatus(id, status);
            return ResponseEntity.ok(application);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().header("X-Error-Message", e.getMessage()).build();
        }
    }
}

