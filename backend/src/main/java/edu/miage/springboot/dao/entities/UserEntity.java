package edu.miage.springboot.dao.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import edu.miage.springboot.dao.entities.UserRoleEntity;
import edu.miage.springboot.dao.entities.UserType;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    private String telephone;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles_map",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<UserRoleEntity> roles = new HashSet<>();


    //pour désactiver un compte (ex: départ d'un employé) sans supprimer 
    //ses données historiques (ex: entretiens passés)
    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    //Enum (CANDIDAT, EMPLOYE) pour filtrer rapidement tes utilisateurs 
    //dans la logique métier sans avoir à vérifier systématiquement leurs rôles de sécurité
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType; 


    // Synchronisation du type d'utilisateur et des rôles associés
    // Evite les jointures lors de requetes de filtrage d'utilisateurs par type
    public void setupAsEmploye(UserRoleEntity employeRole) {
        this.userType = UserType.EMPLOYE;
        this.roles.add(employeRole);
    }

    // Synchronisation du type d'utilisateur et des rôles associés
    // Evite les jointures lors de requetes de filtrage d'utilisateurs par type
    public void setupAsCandidat(UserRoleEntity candidatRole) {
        this.userType = UserType.CANDIDAT;
        this.roles.add(candidatRole);
    }
    

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isEmpty() {
        return (username == null || username.trim().isEmpty()) &&
           (email == null || email.trim().isEmpty()) &&
           (nom == null || nom.trim().isEmpty()) &&
           (prenom == null || prenom.trim().isEmpty());
    }

}
