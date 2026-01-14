package edu.miage.springboot.web.rest.offers;

import edu.miage.springboot.services.impl.offers.ApplicationNoteServiceImpl;
import edu.miage.springboot.web.dtos.offers.ApplicationNoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications/{applicationId}/notes")
public class ApplicationNoteController {

    @Autowired
    private ApplicationNoteServiceImpl noteService;

    /**
     * Spécification 4.3 : Consulter le journal de recrutement.
     * Accessible uniquement aux RH, Admin ou au Demandeur de poste.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN') or @securityService.isJobOfferOwnerFromApplication(#applicationId)")
    public ResponseEntity<List<ApplicationNoteDTO>> getNotes(@PathVariable Long applicationId) {
        List<ApplicationNoteDTO> notes = noteService.getNotesByApplication(applicationId);
        return ResponseEntity.ok(notes);
    }

    /**
     * Spécification 4.3 : Ajouter une note ou un commentaire d'évaluation.
     * Le système identifie automatiquement l'auteur via le token de sécurité.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_RH', 'ROLE_ADMIN') or @securityService.isJobOfferOwnerFromApplication(#applicationId)")
    public ResponseEntity<ApplicationNoteDTO> addNote(
            @PathVariable Long applicationId,
            @RequestBody ApplicationNoteDTO noteDto) {

        ApplicationNoteDTO createdNote = noteService.addNote(applicationId, noteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }
}