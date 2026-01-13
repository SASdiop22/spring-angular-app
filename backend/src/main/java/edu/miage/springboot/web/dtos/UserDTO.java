package edu.miage.springboot.web.dtos;

import edu.miage.springboot.dao.entities.UserTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

@Getter @Setter @NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private UserTypeEnum userType;
    private Set<String> roles; // On renvoie juste les noms des rôles (ex: "ROLE_ADMIN")
    private Long employeProfileId; // On ne renvoie que l'ID pour éviter la boucle
}