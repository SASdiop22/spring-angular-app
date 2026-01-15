package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplicationService {
    // La méthode principale pour postuler
    public ApplicationDTO apply(Long jobOfferId, Long candidateId, String cvUrl, String coverLetter);

    @Transactional
    public ApplicationDTO scheduleInterview(Long applicationId, LocalDateTime date, Long interviewerId, String location);

    // Pour que le recruteur puisse voir toutes les candidatures
    List<ApplicationDTO> findAll();

    public void hireCandidate(Long applicationId);
    List<ApplicationDTO> findByCandidateId(Long candidateId);

    public ApplicationDTO updateStatus(Long applicationId, ApplicationStatusEnum status, String reason);

    // Dans ApplicationServiceImpl.java
    @Transactional
    ApplicationDTO updateStatus(Long id, ApplicationStatusEnum status);

    ApplicationDTO findById(Long id);
}