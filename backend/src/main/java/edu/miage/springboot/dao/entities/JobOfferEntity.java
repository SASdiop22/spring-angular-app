package edu.miage.springboot.dao.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }
}
