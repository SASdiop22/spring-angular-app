package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.CandidatureEntity;
import edu.miage.springboot.dao.entities.CandidatureStatusEnum;
import edu.miage.springboot.dao.entities.JobOfferEntity;
import edu.miage.springboot.dao.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<CandidatureEntity, Long> {

    // Pour le tableau de bord du candidat (Spécification 3.B)
    List<CandidatureEntity> findByCandidate(UserEntity candidate);

    // Pour le suivi par offre (Back-office RH/Demandeur)
    List<CandidatureEntity> findByJob(JobOfferEntity job);

    // Recherche par statut (ex: filtrer tous les "HIRED" ou "REJECTED")
    List<CandidatureEntity> findByCurrentStatus(CandidatureStatusEnum status);

    /**
     * Vérification RGPD (Spécification 3.A)
     * Récupère les candidatures d'un utilisateur créées après une certaine date.
     */
    List<CandidatureEntity> findByCandidateAndCreatedAtAfter(UserEntity candidate, LocalDateTime date);

    /**
     * Calcul de performance / Matching (Spécification 3.A)
     * Trouve les meilleures candidatures pour une offre donnée.
     */
    List<CandidatureEntity> findByJobOrderByMatchingScoreDesc(JobOfferEntity job);

    /**
     * Vérifier si un candidat a déjà postulé à une offre spécifique
     */
    boolean existsByJobAndCandidate(JobOfferEntity job, UserEntity candidate);
}