package edu.miage.springboot.dao.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private JobStatusEnum status;

    private String department;

    @ElementCollection
    private List<String> skillsRequired;

    private Integer remoteDays;

    private Double salary;

}
