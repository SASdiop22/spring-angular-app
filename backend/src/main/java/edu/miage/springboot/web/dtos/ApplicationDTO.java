package edu.miage.springboot.web.dtos;

import java.time.LocalDate;

public class ApplicationDTO {
    private Long id;
    private String status;
    private LocalDate applicationDate;
    private String comment;

    // On ne met que les informations nécessaires, pas l'objet entier
    private Long candidateId;
    private String candidateName;

    private Long jobOfferId;
    private String jobOfferTitle;

    private Long cvId;

    public ApplicationDTO() {}

    // Getters et Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }
    public Long getJobOfferId() { return jobOfferId; }
    public void setJobOfferId(Long jobOfferId) { this.jobOfferId = jobOfferId; }
    public String getJobOfferTitle() { return jobOfferTitle; }
    public void setJobOfferTitle(String jobOfferTitle) { this.jobOfferTitle = jobOfferTitle; }
    public Long getCvId() { return cvId; }
    public void setCvId(Long cvId) { this.cvId = cvId; }
}