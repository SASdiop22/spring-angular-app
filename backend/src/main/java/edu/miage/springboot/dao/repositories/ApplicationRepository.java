package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    List<ApplicationEntity> findByCandidate(UserEntity candidate);

    List<ApplicationEntity> findByJobOrderByMatchingScoreDesc(JobOfferEntity job);

    boolean existsByJobAndCandidate(JobOfferEntity job, UserEntity candidate);

    // Pour la vérification RGPD (Spécification 3.A)
    List<ApplicationEntity> findByCandidateAndCreatedAtAfter(UserEntity candidate, LocalDateTime date);
}