package edu.miage.springboot.dao.repositories.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApplicationNoteRepository extends JpaRepository<ApplicationNoteEntity, Long> {

    /**
     * Récupère toutes les notes d'une candidature spécifique.
     * Triées par date de création (de la plus ancienne à la plus récente)
     * pour suivre le fil du recrutement.
     */
    List<ApplicationNoteEntity> findByApplicationIdOrderByCreatedAtAsc(Long applicationId);

    /**
     * Permet de récupérer les notes rédigées par un auteur spécifique
     * (Utile pour les statistiques RH).
     */
    List<ApplicationNoteEntity> findByAuthorUsername(String username);
}