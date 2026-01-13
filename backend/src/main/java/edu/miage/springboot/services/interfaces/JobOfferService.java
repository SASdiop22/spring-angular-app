package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.JobOfferDTO;

import java.util.List;

public interface JobOfferService {
    List<JobOfferDTO> findAll();
    JobOfferDTO findById(Long id);
    JobOfferDTO updateStatus(Long id, String status);

    JobOfferDTO createJobOffer(JobOfferDTO jobOfferDTO);

    JobOfferDTO updateJobOffer(Long id, JobOfferDTO jobOfferDTO);

    void deleteJobOffer(Long id);

    // Méthode de recherche pour ton rôle spécifique
    List<JobOfferDTO> searchJobOffers(String keyword);
}
