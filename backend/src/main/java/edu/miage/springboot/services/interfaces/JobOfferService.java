package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.dao.entities.JobStatusEnum;
import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.web.dtos.JobOfferDTO;

import java.util.List;

public interface JobOfferService {
    List<JobOfferDTO> findAll();
    JobOfferDTO findById(Long id);
    void validerEtPublierOffre(Long offerId, Double salary, Integer remoteDays, UserEntity currentUser);
    void submitToRh(Long offerId);
    JobOfferDTO createInitialRequest(JobOfferDTO dto, UserEntity currentUser);
    JobOfferDTO updateStatus(Long id, JobStatusEnum status);

    JobOfferDTO createJobOffer(JobOfferDTO jobOfferDTO);

    JobOfferDTO updateJobOffer(Long id, JobOfferDTO jobOfferDTO);

    void deleteJobOffer(Long id);

    // Méthode de recherche pour ton rôle spécifique
    List<JobOfferDTO> searchJobOffers(String keyword);
}
