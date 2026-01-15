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
    private ApplicationStatusEnum status;
    private String reason;
    // getters/setters
}