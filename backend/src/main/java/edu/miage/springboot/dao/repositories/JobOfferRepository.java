package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.EmployeEntity;
import edu.miage.springboot.dao.entities.JobOfferEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {
    List<JobOfferEntity> findByStatus(String status);
    List<JobOfferEntity> findByCreator(EmployeEntity creator);
}


