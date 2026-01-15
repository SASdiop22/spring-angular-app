package edu.miage.springboot.web.dtos.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationStatusUpdateDTO {
    @NotNull
    private ApplicationStatusEnum status;

    // Spec 4.4 : Motif de rejet
    private String reason;

    // Spec 4.2 : Données logistiques
    private LocalDateTime meetingDate;
    private String meetingLocation;

    // Getters / Setters
}