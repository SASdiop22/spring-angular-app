package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.ApplicationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
    // La méthode principale pour postuler
    void apply(Long jobOfferId, Long candidateId, MultipartFile cvFile);

    // Pour que le recruteur puisse voir toutes les candidatures
    List<ApplicationDTO> findAll();
}