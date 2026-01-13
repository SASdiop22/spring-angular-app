
package edu.miage.springboot.web.dtos;

import edu.miage.springboot.dao.entities.ApplicationStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDTO {
    private Long id;
    private ApplicationStatusEnum status;
    private LocalDateTime applicationDate;
    private String comment;

    // Informations sur le candidat
    private Long candidateId;
    private String candidateName;

    // Informations sur l'offre
    private Long jobOfferId;
    private String jobOfferTitle;

    // CV et lettre de motivation
    private String cvUrl;
    private String coverLetter;

    // Informations supplémentaires
    private LocalDateTime meetingDate;
    private Integer matchingScore;
    private LocalDateTime createdAt;
}