// backend/src/main/java/edu/miage/springboot/web/dtos/JobOfferDTO.java
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

}