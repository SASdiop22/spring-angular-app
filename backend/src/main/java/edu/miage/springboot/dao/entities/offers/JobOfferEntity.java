package edu.miage.springboot.dao.entities.offers;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "job_offers")
public class JobOfferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    @Column(length = 2000)
    private String description;
    private LocalDate deadline;
    //@Enumerated(EnumType.STRING)
    private String department;
    private String location;
    private Double salaryRange; // Enrichi par les RH
    private Integer remoteDays; // Enrichi par les RH - Spécification 2.A

    @Enumerated(EnumType.STRING)
    private JobStatusEnum status = JobStatusEnum.DRAFT;
    // Compétences clés (Tags : Java, Agile, etc.) - Spécification 2.A
    @ElementCollection
    @CollectionTable(name = "job_skills", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> skillsRequired = new ArrayList<>();
    //L'employé (Demandeur) qui a créé le besoin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private EmployeEntity creator;

    //Pour programmer le rendez-vous
    @Column(name = "meeting_date")
    private java.time.LocalDateTime meetingDate;

    //La personne qui va interviewer le candidat
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private EmployeEntity interviewer;

    //Le feedback apres l'entretien'
    @Column(name = "technical_feedback", columnDefinition = "TEXT")
    private String technicalFeedback;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void submitForApproval() {
        if (this.status == JobStatusEnum.DRAFT) {
            this.status = JobStatusEnum.PENDING;
        }
    }

    public void validateAndPublish(Double salaryRange, Integer remoteDays) {
        this.salaryRange = salaryRange;
        this.remoteDays = remoteDays;
        this.status = JobStatusEnum.OPEN;
        this.publishedAt = LocalDateTime.now();
    }

}
