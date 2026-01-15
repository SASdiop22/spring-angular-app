package edu.miage.springboot.web.dtos.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class CandidatDTO {
    // ID partagé avec User
    private Long id;

    // Champs venant de UserEntity
    private String email;
    private String username;

    // Champs venant de CandidatEntity
    //private String firstName;
    //private String lastName;
    private String telephone;
    private String ville;
    private LocalDateTime consentDate;
    private boolean archived;

    // Champ calculé pour faciliter le travail du Front-end
    private boolean rgpdCompliant;
}