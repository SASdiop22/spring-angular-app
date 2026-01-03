package edu.miage.springboot.dao.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "applications")
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String status; // Exemple : "RECU", "ENTRETIEN", "ACCEPTE", "REFUSE"

    private LocalDate applicationDate;

    @Column(length = 1000)
    private String comment;

    // --- RELATIONS ---

    // Plusieurs candidatures peuvent appartenir à un seul candidat
    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private UserEntity candidate;

    // Plusieurs candidatures peuvent viser la même offre d'emploi
    @ManyToOne
    @JoinColumn(name = "job_offer_id", nullable = false)
    private JobOfferEntity jobOffer;

    // Une candidature est généralement liée à un seul CV spécifique
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cv_file_id")
    private FileEntity cv;

    public ApplicationEntity() {
        this.applicationDate = LocalDate.now();
        this.status = "RECU"; // Statut par défaut
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserEntity getCandidate() {
        return candidate;
    }

    public void setCandidate(UserEntity candidate) {
        this.candidate = candidate;
    }

    public JobOfferEntity getJobOffer() {
        return jobOffer;
    }

    public void setJobOffer(JobOfferEntity jobOffer) {
        this.jobOffer = jobOffer;
    }

    public FileEntity getCv() {
        return cv;
    }

    public void setCv(FileEntity cv) {
        this.cv = cv;
    }
}