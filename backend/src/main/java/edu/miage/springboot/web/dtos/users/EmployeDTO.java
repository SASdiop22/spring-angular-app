package edu.miage.springboot.web.dtos.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class EmployeDTO {
    private Long id;
    private String poste;
    private String departement;
    private String telephoneInterne;

    // Informations utilisateur "aplaties" pour éviter la dépendance circulaire
    private String username;
    private String email;
    private String nomComplet; // Pratique pour le front (prenom + nom)

    private boolean demandeurDePoste;
    private boolean rhPrivilege;
    private boolean adminPrivilege;

    // --- Champs pour le Référent ---
    private Long referentId;
    private String referentNomComplet;
}