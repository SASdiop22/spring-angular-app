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
    ApplicationDTO scheduleInterview(Long applicationId, LocalDateTime date, Long interviewerId);

    // Pour que le recruteur puisse voir toutes les candidatures
    List<ApplicationDTO> findAll();

    List<ApplicationDTO> findByCandidateId(Long candidateId);

    ApplicationDTO updateStatus(Long id, ApplicationStatusEnum status);
}