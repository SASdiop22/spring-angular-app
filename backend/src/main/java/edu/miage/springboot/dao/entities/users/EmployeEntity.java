package edu.miage.springboot.dao.entities.users;

import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employes")
@Getter 
@Setter 
@NoArgsConstructor
public class EmployeEntity {

    @Id
    private Long id; // Partagé avec UserEntity via @MapsId

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UserEntity user;

    @Column(nullable = false)
    private String poste;

    @Column(nullable = false)
    private String departement; // Ex: IT, RH, Marketing (Spécification 2.A)

    private String telephoneInterne;

    @Column(columnDefinition = "boolean default true")
    private boolean demandeurDePoste; 
    @Column(columnDefinition = "boolean default false")
    private boolean rhPrivilege; 
    @Column(columnDefinition = "boolean default false")
    private boolean adminPrivilege;

    // Relation avec les offres dont il est à l'origine (Spécification 2.A)
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<JobOfferEntity> jobsDemandes = new ArrayList<>();

    /**
     * Le manager direct (Référent)
     * Relation auto-référencée : un employé a UN référent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referent_id")
    private EmployeEntity referent;

    // Relation finale une fois le candidat embauché (Spécification 5 - Succès)
    // Liste des candidats que cet employé "encadre" ou a recruté
    @OneToMany(mappedBy = "referentEmploye")
    private List<UserEntity> recruesLiees = new ArrayList<>();

    /**
     * AJOUT MANQUANT : La liste des subordonnés hiérarchiques.
     * C'est cette liste qui est utilisée par employeService.getRecruits(id).
     */
    @OneToMany(mappedBy = "referent")
    private List<EmployeEntity> subordinates = new ArrayList<>();
    /**
     * Helper pour lier l'utilisateur et l'employé
     */
    public EmployeEntity(UserEntity user, String poste, String departement) {
        this.user = user;
        this.id = user.getId();
        this.poste = poste;
        this.departement = departement;
    }

}