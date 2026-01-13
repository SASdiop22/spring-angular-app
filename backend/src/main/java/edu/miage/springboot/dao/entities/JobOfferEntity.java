package edu.miage.springboot.dao.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter 
@Setter 
@NoArgsConstructor
public class JobOfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    private LocalDate deadline;
    private String location;
    private Double salary;

    @Column(nullable = false)
    private String department; // Service (ex: IT, RH) - Spécification 2.A

    private Double salaryRange; // Enrichi par les RH
    
    private Integer remoteDays; // Enrichi par les RH - Spécification 2.A

    @Enumerated(EnumType.STRING)
    private JobStatusEnum status = JobStatusEnum.DRAFT;

    // L'employé (Demandeur) qui a créé le besoin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private EmployeEntity creator;

    // Compétences clés (Tags : Java, Agile, etc.) - Spécification 2.A
    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> skillsRequired = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Méthode pour la spécification 2.A (Validation RH)
    public void submitForApproval() {
        if (this.status == JobStatusEnum.DRAFT) {
            this.status = JobStatusEnum.PENDING;
        }
    }

    public void validateAndPublish(Double salary, Integer remoteDays) {
        this.salaryRange = salary;
        this.remoteDays = remoteDays;
        this.status = JobStatusEnum.OPEN;
        this.publishedAt = java.time.LocalDateTime.now();
    }
}
