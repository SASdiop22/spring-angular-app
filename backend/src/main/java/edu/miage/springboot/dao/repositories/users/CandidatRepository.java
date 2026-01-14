package edu.miage.springboot.dao.repositories.users;

import edu.miage.springboot.dao.entities.users.CandidatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CandidatRepository extends JpaRepository<CandidatEntity, Long> {

    /**
     * Recherche un candidat par l'email de son compte User
     */
    Optional<CandidatEntity> findByUserEmail(String email);

    /**
     * Spécification 3.A : RGPD
     * Récupère tous les candidats dont le consentement a expiré (plus de 2 ans).
     * Utile pour les scripts de nettoyage ou les alertes RH.
     */
    List<CandidatEntity> findByConsentDateBefore(LocalDateTime threshold);

    /**
     * Recherche par nom ou prénom (Utile pour le back-office RH)
     */
    List<CandidatEntity> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String lastName, String firstName);

    /**
     * Vérifie si un candidat est conforme au RGPD avant de lui permettre de postuler.
     */
    @Query("SELECT (COUNT(c) > 0) FROM CandidatEntity c WHERE c.id = :id AND c.consentDate > :threshold")
    boolean isConsentValid(@Param("id") Long id, @Param("threshold") LocalDateTime threshold);

    Optional<CandidatEntity> findByUserUsername(String s);
}