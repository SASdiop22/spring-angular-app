package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.EmployeEntity;
import edu.miage.springboot.dao.entities.JobOfferEntity;
import edu.miage.springboot.dao.entities.JobStatusEnum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobOfferRepository extends JpaRepository<JobOfferEntity, Long> {
    List<JobOfferEntity> findByStatus(JobStatusEnum status);
    List<JobOfferEntity> findByCreator(EmployeEntity creator);
    List<JobOfferEntity> findByDepartment(String department);
    // Recherche textuelle pour les candidats
    List<JobOfferEntity> findByTitleContainingIgnoreCaseAndStatus(String title, JobStatusEnum status);
    // Pour le tableau de bord RH : voir tout ce qui nécessite une action (DRAFT, PENDING)
    List<JobOfferEntity> findByStatusIn(List<JobStatusEnum> statuses);
}


