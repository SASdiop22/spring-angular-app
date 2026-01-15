package edu.miage.springboot.dao.repositories.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    List<ApplicationEntity> findByCandidate(CandidatEntity candidate);

    List<ApplicationEntity> findByJobOrderByMatchingScoreDesc(JobOfferEntity job);

    boolean existsByJobAndCandidate(JobOfferEntity job, CandidatEntity candidate);

    // Pour la vérification RGPD (Spécification 3.A)
    List<ApplicationEntity> findByCandidateAndCreatedAtAfter(CandidatEntity candidate, LocalDateTime date);

    List<ApplicationEntity> findByCandidateId(Long candidateId);

    boolean existsByJobIdAndCandidateId(Long jobOfferId, Long candidateId);
}