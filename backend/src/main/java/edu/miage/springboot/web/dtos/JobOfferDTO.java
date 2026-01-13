package edu.miage.springboot.web.dtos;

import java.time.LocalDate;

public class JobOfferDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate deadline;
    // Champs ajoutés
    private String location;
    private Double salary;
    private String status;

    // Constructeur vide
    public JobOfferDTO() {}

    // Getters et Setters pour TOUS les champs...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}