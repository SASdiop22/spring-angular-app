package edu.miage.springboot.dao.repositories.offers;

import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {
    // Recherche par mot-clé dans le titre ou la description
    List<JobOfferEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    // Filtrage par localisation
    List<JobOfferEntity> findByLocationContainingIgnoreCase(String location);

    List<JobOfferEntity> findByStatus(JobStatusEnum jobStatusEnum);

    List<JobOfferEntity> findByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCase(
            JobStatusEnum status1, String title,
            JobStatusEnum status2, String description
    );

    // Filtrage par plusieurs statuts
    List<JobOfferEntity> findByStatusIn(List<JobStatusEnum> statuses);
}


