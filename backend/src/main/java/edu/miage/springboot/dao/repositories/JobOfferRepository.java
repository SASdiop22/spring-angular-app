package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.JobOfferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {
}
