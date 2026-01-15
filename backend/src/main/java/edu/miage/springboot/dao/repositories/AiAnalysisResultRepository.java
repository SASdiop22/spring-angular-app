package edu.miage.springboot.dao.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.miage.springboot.dao.entities.AiAnalysisResultEntity;

public interface AiAnalysisResultRepository
        extends JpaRepository<AiAnalysisResultEntity, Long> {

    List<AiAnalysisResultEntity> findByJobOfferId(Long jobOfferId);

    List<AiAnalysisResultEntity> findByApplicationId(Long applicationId);
}
