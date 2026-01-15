package edu.miage.springboot.web.dtos.offers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String status;
    private LocalDateTime applicationDate;
    private String candidateName;
    private Long candidateId;
    private Long jobOfferId;
    private String jobOfferTitle;
    private String cvUrl;
    private String coverLetter;
    private Integer matchingScore;

    // --- Nouveaux champs pour les spécifications 4.B et 5 ---
    private LocalDateTime meetingDate;    // Pour les entretiens et tests
    private String rejectionReason;        // Pour le motif de rejet (Spec 4.B)
    private List<ApplicationNoteDTO> notes;    // Pour le suivi interne
    private LocalDateTime createdAt;
}