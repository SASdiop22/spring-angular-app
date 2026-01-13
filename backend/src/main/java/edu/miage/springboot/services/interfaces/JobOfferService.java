package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.dao.entities.JobStatusEnum;
import edu.miage.springboot.web.dtos.JobOfferDTO;

import java.util.List;

public interface JobOfferService {
    List<JobOfferDTO> findAll();
    JobOfferDTO findById(Long id);

    public JobOfferDTO updateStatus(Long id, JobStatusEnum status);

    JobOfferDTO createJobOffer(JobOfferDTO jobOfferDTO);

    JobOfferDTO updateJobOffer(Long id, JobOfferDTO jobOfferDTO);

    void deleteJobOffer(Long id);

    // Méthode de recherche pour ton rôle spécifique
    List<JobOfferDTO> searchJobOffers(String keyword);

    // --- Spec 2.A & 2.B : Visibilité publique ---
    List<JobOfferDTO> findAllOpen();

    JobOfferDTO enrichAndPublish(Long id, Double salary, Integer remoteDays);
}
