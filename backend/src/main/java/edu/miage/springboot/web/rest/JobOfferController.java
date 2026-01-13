package edu.miage.springboot.web.rest;

import edu.miage.springboot.dao.entities.JobStatusEnum;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.web.dtos.JobOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joboffers")
public class JobOfferController {

    @Autowired
    private JobOfferService jobOfferService;

    // --- ACCÈS PUBLIC (Visiteurs & Candidats) ---

    @GetMapping
    public List<JobOfferDTO> getAllPublished() {
        // Strict respect de la Spec 2.A : Seules les offres OPEN sont visibles publiquement
        return jobOfferService.findAllOpen();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobOfferService.findById(id));
    }

    @GetMapping("/search")
    public List<JobOfferDTO> search(@RequestParam String keyword) {
        return jobOfferService.searchJobOffers(keyword);
    }

    // --- ACCÈS EMPLOYÉ / DEMANDEUR (Spec 1 & 2.A) ---

    @PostMapping
    //@PreAuthorize("hasRole('EMPLOYE')")
    public ResponseEntity<JobOfferDTO> create(@RequestBody JobOfferDTO jobOfferDTO) {
        // Création initiale en statut DRAFT
        return new ResponseEntity<>(jobOfferService.createJobOffer(jobOfferDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/submit")
    //@PreAuthorize("hasRole('EMPLOYE') and @securityService.isOwner(#id)")
    public ResponseEntity<JobOfferDTO> submitForApproval(@PathVariable Long id) {
        // Passage de DRAFT à PENDING (Spec 2.A)
        return ResponseEntity.ok(jobOfferService.updateStatus(id, JobStatusEnum.PENDING));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasAnyRole('RH', 'ADMIN') or @securityService.isOwner(#id)")
    public ResponseEntity<JobOfferDTO> update(@PathVariable Long id, @RequestBody JobOfferDTO jobOfferDTO) {
        return ResponseEntity.ok(jobOfferService.updateJobOffer(id, jobOfferDTO));
    }

    // --- ACCÈS RH : VALIDATION ET PUBLICATION (Spec 2.A & 4.A) ---

    /**
     * Spécification 2.A : Validation et Enrichissement.
     * Le RH est le seul à pouvoir définir le salaire et le télétravail pour passer en OPEN.
     */
    @PatchMapping("/{id}/publish")
    //@PreAuthorize("hasRole('RH')")
    public ResponseEntity<JobOfferDTO> validateAndPublish(
            @PathVariable Long id,
            @RequestParam Double salary,
            @RequestParam Integer remoteDays) {

        JobOfferDTO publishedOffer = jobOfferService.enrichAndPublish(id, salary, remoteDays);
        return ResponseEntity.ok(publishedOffer);
    }


    @GetMapping("/privilege")
    //@PreAuthorize("hasAnyRole('RH', 'ADMIN')") // Spec 1 & 4.A
    public List<JobOfferDTO> getAllWithPrivilege() {
        // Retourne toutes les offres (DRAFT, PENDING, OPEN, CLOSED, FILLED)
        return jobOfferService.findAll();
    }
    // --- ADMINISTRATION ET CLÔTURE (Spec 2.B) ---

    @PatchMapping("/{id}/close")
    //@PreAuthorize("hasAnyRole('RH', 'ADMIN')")
    public ResponseEntity<JobOfferDTO> closeOffer(@PathVariable Long id) {
        // Passage manuel en statut CLOSED (Spec 2.B)
        return ResponseEntity.ok(jobOfferService.updateStatus(id, JobStatusEnum.CLOSED));
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize("hasAnyRole('RH', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobOfferService.deleteJobOffer(id);
        return ResponseEntity.noContent().build();
    }
}