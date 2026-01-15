package edu.miage.springboot.web.dtos.offers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationNoteDTO {

    private Long id;

    /**
     * Le nom ou username de l'auteur (RH ou Demandeur)
     * pour savoir qui a écrit la note.
     */
    private String authorName;

    /**
     * L'étape du recrutement lors de la saisie (ex: "Entretien Technique").
     */
    private String stepName;

    /**
     * Le contenu textuel de la note (strictement interne).
     */
    private String content;

    /**
     * Date de création de la note pour le suivi chronologique.
     */
    private LocalDateTime createdAt;

    /**
     * ID de la candidature associée (optionnel, utile pour le mapping).
     */
    private Long applicationId;
}