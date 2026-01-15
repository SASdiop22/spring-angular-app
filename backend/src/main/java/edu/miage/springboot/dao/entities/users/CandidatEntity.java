package edu.miage.springboot.dao.entities.users;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidats")
@Getter
@Setter
@NoArgsConstructor
public class CandidatEntity {

    @Id
    private Long id; // Partagé avec UserEntity via @MapsId

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**@Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;*/

    private String telephone;

    private String ville;

    private boolean archived;

    /**
     * Spécification 3.A : RGPD
     * Date du dernier consentement (doit être < 2 ans pour postuler).
     */
    @Column(name = "consent_date", nullable = false)
    private LocalDateTime consentDate;

    // Lien avec les candidatures (Spécification 3.B)
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationEntity> applications = new ArrayList<>();

    // Compétences du candidat
    @ElementCollection
    @CollectionTable(name = "candidat_skills", joinColumns = @JoinColumn(name = "candidat_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();
    /**
     * Méthode utilitaire pour valider la règle RGPD des 2 ans
     */
    public boolean isRgpdCompliant() {
        return consentDate != null && consentDate.isAfter(LocalDateTime.now().minusYears(2));
    }

    @PrePersist
    protected void onCreate() {
        if (this.consentDate == null) {
            this.consentDate = LocalDateTime.now();
        }
    }

}