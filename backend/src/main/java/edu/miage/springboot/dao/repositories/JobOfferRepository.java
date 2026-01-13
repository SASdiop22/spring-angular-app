package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.JobOfferEntity;
import edu.miage.springboot.dao.entities.JobStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {
    // Recherche par mot-clé dans le titre ou la description
    List<JobOfferEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    // Filtrage par localisation
    List<JobOfferEntity> findByLocationContainingIgnoreCase(String location);

    List<JobOfferEntity> findByStatus(JobStatusEnum jobStatusEnum);
}


