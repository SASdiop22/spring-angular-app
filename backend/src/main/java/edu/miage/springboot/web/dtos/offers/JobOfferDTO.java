package edu.miage.springboot.web.dtos.offers;

import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor
public class JobOfferDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate deadline;
    private String department;
    private Double salaryRange; // Spécification 2.A (Enrichi par RH)

    private Integer remoteDays; // Spécification 2.A (Enrichi par RH)
    private JobStatusEnum status;

    // Correction : On peut garder l'ID, mais il est souvent utile
    // d'avoir le nom du créateur pour l'affichage sans charger l'entité
    private Long creatorId;
    private String creatorName;

    private List<String> skillsRequired;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private String location;
}