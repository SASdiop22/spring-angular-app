package edu.miage.springboot.dao.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import edu.miage.springboot.dao.entities.UserRoleEntity;
import edu.miage.springboot.dao.entities.UserTypeEnum;

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
    

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmployeEntity employeProfile;
    
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

    @ManyToOne
    @JoinColumn(name = "referent_employe_id")
    private EmployeEntity referentEmploye;


    //Enum (CANDIDAT, EMPLOYE) pour filtrer rapidement tes utilisateurs 
    //dans la logique métier sans avoir à vérifier systématiquement leurs rôles de sécurité
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserTypeEnum userType;


    //Synchronisation bidirectionnelle de la relation avec EmployeEntity
    public void setReferentEmploye(EmployeEntity employe) {
    if (this.referentEmploye != null) {
        this.referentEmploye.getRecruesLiees().remove(this);
    }
    this.referentEmploye = employe;
    if (employe != null && !employe.getRecruesLiees().contains(this)) {
        employe.getRecruesLiees().add(this);
    }

    public boolean isPresent() {
        return this.id != null;
    }
}


    // Synchronisation du type d'utilisateur et des rôles associés
    // Evite les jointures lors de requetes de filtrage d'utilisateurs par type
    public void setupAsEmploye(UserRoleEntity employeRole) {
        this.userType = UserTypeEnum.EMPLOYE;
        this.roles.add(employeRole);
    }

    // Synchronisation du type d'utilisateur et des rôles associés
    // Evite les jointures lors de requetes de filtrage d'utilisateurs par type
    public void setupAsCandidat(UserRoleEntity candidatRole) {
        this.userType = UserTypeEnum.CANDIDAT;
        this.roles.add(candidatRole);
    }

    public void setupAsRhPrivilege(UserRoleEntity rhRole) {
        this.setUserType(UserTypeEnum.RH);
        this.getRoles().add(rhRole);
    }

    public void setupAsDemandeurDePoste(UserRoleEntity demandeurRole) {
        this.setUserType(UserTypeEnum.EMPLOYE);
        this.getRoles().add(demandeurRole);
    }

    public void setupAsAdmin(UserRoleEntity adminRole) {
        this.setUserType(UserTypeEnum.ADMIN);
        this.getRoles().add(adminRole);
    }

    public void downgradeFromPrivilege(UserRoleEntity role) {
        this.getRoles().remove(role);
        this.setUserType(UserTypeEnum.EMPLOYE);
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


    public EmployeEntity getEmployeProfile() {
       return this.employeProfile;
    }

}
