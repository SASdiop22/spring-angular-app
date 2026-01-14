package edu.miage.springboot.web.dtos.offers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String status; // Correspond au @Mapping currentStatus -> status
    private LocalDateTime applicationDate; // Utilisez LocalDateTime pour matcher l'entité
    private String candidateName;
    private Long candidateId;
    private Long jobOfferId;
    private String jobOfferTitle;
    private String cvUrl;
}