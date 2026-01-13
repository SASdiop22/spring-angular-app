package edu.miage.springboot.web.dtos;

import edu.miage.springboot.dao.entities.JobStatusEnum;
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
    private Double salaryRange; // Salaire annuel brut
    private Integer remoteDays; // Jours de télétravail
    private JobStatusEnum status;
    private Long creatorId;     // ID de l'employé demandeur
    private List<String> skillsRequired; // Tags techniques (Java, Agile, etc.)
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private String location;
}