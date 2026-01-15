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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CandidatRepository candidatRepository;

    /**
     * Spécification 3.A : Un candidat postule.
     * La vérification RGPD (< 2 ans) doit être gérée dans le service (applicationService.apply).
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
            // On vérifie si le candidat n'est pas déjà archivé (embauché)
            CandidatEntity candidate = candidatRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

            if (candidate.isArchived()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null); // Ou un message : "Profil archivé, redirection vers espace employé"
            }
            ApplicationDTO newApplication = applicationService.apply(jobOfferId, candidateId, cvUrl, coverLetter);
            return ResponseEntity.status(HttpStatus.CREATED).body(newApplication);
        }catch (Exception e) {
            e.printStackTrace(); // Pour voir l'erreur exacte dans la console Spring
            return ResponseEntity.badRequest().header("X-Error-Reason", e.getMessage()).build();
        }
    }

    /**
     * Spécification 4.B : Mise à jour du statut avec motif optionnel.
    */

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody ApplicationStatusUpdateDTO dto) {
        return ResponseEntity.ok(applicationService.updateStatus(id, dto.getStatus(), dto.getReason()));
    }

    /**
     * Spécification 5 : Finalisation de l'embauche.
     */
    @PostMapping("/{id}/hire")
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN')")
    public ResponseEntity<Void> hireCandidate(@PathVariable Long id) {
        applicationService.hireCandidate(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Spécification 4.A : Planifier un entretien.
     */
    @PatchMapping("/{id}/schedule-interview")
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN')")
    public ResponseEntity<ApplicationDTO> scheduleInterview(
            @PathVariable Long id,
            @RequestParam LocalDateTime date,
            @RequestParam Long interviewerId,
            @RequestParam String location) {
        return ResponseEntity.ok(applicationService.scheduleInterview(id, date, interviewerId, location));
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN') or (hasAuthority('ROLE_CANDIDAT') and @securityService.isApplicationOwner(#id))")
    public ResponseEntity<ApplicationDTO> getApplicationById(@PathVariable Long id) {
        try {
            // Le service doit implémenter une méthode findById ou getById
            ApplicationDTO application = applicationService.findById(id);
            return ResponseEntity.ok(application);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Spécification 4.A : Seuls les RH et Admin voient la liste globale.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN')")
    public List<ApplicationDTO> getAllApplications() {
        return applicationService.findAll();
    }

    /**
     * Spécification 3.B : Un candidat peut voir ses propres candidatures.
     */
    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or ((hasAuthority('ROLE_CANDIDAT') and  @securityService.isApplicationOwner(#candidateId)))")
    public List<ApplicationDTO> getApplicationsByCandidate(@PathVariable Long candidateId) {
        return applicationService.findByCandidateId(candidateId);
    }

}