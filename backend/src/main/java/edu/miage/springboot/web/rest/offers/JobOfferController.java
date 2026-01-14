package edu.miage.springboot.web.rest.offers;

import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.web.dtos.offers.JobOfferDTO;
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

    /**
     * Spécification 2.A : Seules les offres OPEN sont accessibles publiquement.
     */
    @GetMapping
    public List<JobOfferDTO> getAllPublished() {
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

    // --- ACCÈS EMPLOYÉ / DEMANDEUR (Spec 2.A) ---

    /**
     * Spécification 2.A : Un Employé (demandeur de poste) crée une offre en DRAFT.
     * Note: On autorise ADMIN/RH par extension.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RH', 'ROLE_EMPLOYE')")
    public ResponseEntity<JobOfferDTO> create(@RequestBody JobOfferDTO jobOfferDTO) {
        return new ResponseEntity<>(jobOfferService.createJobOffer(jobOfferDTO), HttpStatus.CREATED);
    }

    /**
     * Spécification 2.A : Le créateur peut modifier son brouillon ou passer en PENDING.
     * Utilise securityService.isOwner pour vérifier que c'est bien le créateur.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RH') or @securityService.isJobOfferOwner(#id)")
    public ResponseEntity<JobOfferDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam JobStatusEnum status) {
        return ResponseEntity.ok(jobOfferService.updateStatus(id, status));
    }

    // --- ACCÈS RH / ADMIN (Spec 4.A) ---

    /**
     * Spécification 2.A & 4.A : Seul le RH (ou Admin) peut enrichir (salaire/télétravail)
     * et publier l'offre (Passage en OPEN).
     */
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RH')")
    public ResponseEntity<JobOfferDTO> validateAndPublish(
            @PathVariable Long id,
            @RequestParam Double salary,
            @RequestParam Integer remoteDays) {
        JobOfferDTO publishedOffer = jobOfferService.enrichAndPublish(id, salary, remoteDays);
        return ResponseEntity.ok(publishedOffer);
    }

    /**
     * Spécification 4.A : Seuls les RH/ADMIN voient toutes les offres (DRAFT, PENDING...).
     */
    @GetMapping("/privilege")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RH')")
    public List<JobOfferDTO> getAllWithPrivilege() {
        return jobOfferService.findAll();
    }

    /**
     * Spécification 2.B : Clôture administrative de l'offre.
     */
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RH')")
    public ResponseEntity<JobOfferDTO> closeOffer(@PathVariable Long id) {
        return ResponseEntity.ok(jobOfferService.updateStatus(id, JobStatusEnum.CLOSED));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RH')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobOfferService.deleteJobOffer(id);
        return ResponseEntity.noContent().build();
    }
}