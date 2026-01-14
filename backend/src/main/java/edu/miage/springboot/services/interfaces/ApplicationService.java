package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;

import java.util.List;

public interface ApplicationService {
    // La méthode principale pour postuler
    public ApplicationDTO apply(Long jobOfferId, Long candidateId, String cvUrl, String coverLetter);

    // Pour que le recruteur puisse voir toutes les candidatures
    List<ApplicationDTO> findAll();

    List<ApplicationDTO> findByCandidateId(Long candidateId);

    ApplicationDTO updateStatus(Long id, ApplicationStatusEnum status);
}