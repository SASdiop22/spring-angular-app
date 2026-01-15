package edu.miage.springboot.dao.entities.offers;

import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.postgresql.core.QueryExecutor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applications")
@Getter @Setter @NoArgsConstructor
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private JobOfferEntity job;

    // CORRECTION : Type changé de UserEntity à CandidatEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidatEntity candidate;

    @Column(name = "cv_url", nullable = false)
    private String cvUrl;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    private ApplicationStatusEnum currentStatus = ApplicationStatusEnum.RECEIVED;

    @Column(name = "meeting_date")
    private LocalDateTime meetingDate;

    @Column(name = "meeting_location")
    private String meetingLocation;

    @Column(name = "matching_score")
    private Integer matchingScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    // L'employé chargé de faire passer l'entretien
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interviewer_id")
    private EmployeEntity interviewer;

    // Le retour technique suite à l'entretien
    @Column(name = "technical_feedback", columnDefinition = "TEXT")
    private String technicalFeedback;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<ApplicationNoteEntity> recruitmentNotes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}