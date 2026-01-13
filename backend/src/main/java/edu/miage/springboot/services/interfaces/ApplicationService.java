package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.web.dtos.ApplicationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
    // La méthode principale pour postuler
    public void apply(Long jobOfferId, Long candidateId, String cvUrl, String coverLetter);

    public void finalizeRecruitment(Long applicationId, UserEntity currentUser);
    // Pour que le recruteur puisse voir toutes les candidatures
    List<ApplicationDTO> findAll();
}