package edu.miage.springboot.web.rest.users;

import edu.miage.springboot.services.interfaces.CandidatService;
import edu.miage.springboot.services.impl.security.SecurityService;
import edu.miage.springboot.web.dtos.users.CandidatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
@CrossOrigin(origins = "*")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private SecurityService securityService;

    /**
     * Spécification 1 : Consultation des profils.
     * Uniquement pour RH (rhPrivilege) ou Administrateurs.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('RH', 'ADMIN')")
    public ResponseEntity<List<CandidatDTO>> getAllCandidates() {
        return ResponseEntity.ok(candidatService.findAll());
    }

    /**
     * Endpoint spécifique pour résoudre le problème du script de test .http
     * Permet au candidat connecté de récupérer son propre ID sans erreur 403.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CANDIDAT')")
    public ResponseEntity<CandidatDTO> getCurrentCandidate(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(candidatService.findByUsername(userDetails.getUsername()));
    }

    /**
     * Accès individuel.
     * Le candidat ne peut voir que son profil (#id == principal.id).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('RH', 'ADMIN') or (hasRole('CANDIDAT') and @securityService.isOwner(#id))")
    public ResponseEntity<CandidatDTO> getCandidateById(@PathVariable Long id) {
        return ResponseEntity.ok(candidatService.findById(id));
    }

    /**
     * Mise à jour du profil.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CANDIDAT') and @securityService.isOwner(#id))")
    public ResponseEntity<CandidatDTO> updateProfile(@PathVariable Long id, @RequestBody CandidatDTO dto) {
        return ResponseEntity.ok(candidatService.updateProfile(id, dto));
    }

    /**
     * Spécification 3.A : Renouvellement du consentement RGPD.
     * Permet au candidat de réinitialiser le compteur des 2 ans.
     */
    @PatchMapping("/{id}/renew-consent")
    @PreAuthorize("hasRole('CANDIDAT') and @securityService.isOwner(#id)")
    public ResponseEntity<Void> renewConsent(@PathVariable Long id) {
        candidatService.renewConsent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Suppression (Droit à l'oubli RGPD).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CANDIDAT') and @securityService.isOwner(#id))")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidatService.deleteCandidat(id);
        return ResponseEntity.noContent().build();
    }
}