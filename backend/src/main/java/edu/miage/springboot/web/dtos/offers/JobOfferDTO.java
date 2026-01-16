package edu.miage.springboot.web.dtos.offers;

import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
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
    private String companyName;
    private String companyDescription;
    private String contractType;
    private LocalDate deadline;
    private String department;
    private Double salary;
    private Integer remoteDays;
    private JobStatusEnum status;
    private Long creatorId;
    private String creatorName;

    private List<String> skillsRequired;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private String location;
}