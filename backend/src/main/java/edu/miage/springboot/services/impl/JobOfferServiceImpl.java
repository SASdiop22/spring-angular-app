package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.CandidatureEntity;
import edu.miage.springboot.dao.entities.JobOfferEntity;
import edu.miage.springboot.dao.entities.JobStatusEnum;
import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.repositories.JobOfferRepository;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.utils.mappers.JobOfferMapper;
import edu.miage.springboot.web.dtos.JobOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobOfferServiceImpl implements JobOfferService {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private JobOfferMapper jobOfferMapper;

    @Override
    public List<JobOfferDTO> findAll() {
        return jobOfferMapper.entitiesToDtos(jobOfferRepository.findAll());
    }

    @Override
    public JobOfferDTO findById(Long id) {
        return jobOfferRepository.findById(id).map(jobOfferMapper::entityToDto).orElseThrow(() -> new RuntimeException("Offre introuvable"));
    }

    @Override
    public JobOfferDTO createInitialRequest(JobOfferDTO dto, UserEntity currentUser) {
        // Vérification du privilège demandeur (Spécification 1 & 2.A)
        if (!currentUser.getEmployeProfile().isDemandeurDePoste()) {
            throw new AccessDeniedException("Seul un demandeur de poste peut créer une offre.");
        }

        JobOfferEntity offer = jobOfferMapper.dtoToEntity(dto);
        offer.setCreator(currentUser.getEmployeProfile());
        offer.setStatus(JobStatusEnum.DRAFT); // Forcer le statut initial
        
        return jobOfferMapper.entityToDto(jobOfferRepository.save(offer));
    }

    @Override
    public void submitToRh(Long offerId) {
        JobOfferEntity offer = jobOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));
        offer.submitForApproval(); // Passage à PENDING
        jobOfferRepository.save(offer);
    }

    @Override
    public void validerEtPublierOffre(Long offerId, Double salary, Integer remoteDays, UserEntity currentUser) {
        // Vérification stricte du privilège RH (Spécification 1 & 4.A)
        if (!currentUser.getEmployeProfile().isRhPrivilege()) {
            throw new AccessDeniedException("Seul un membre RH peut publier cette offre.");
        }

        JobOfferEntity offer = jobOfferRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        if (offer.getStatus() != JobStatusEnum.PENDING) {
            throw new IllegalStateException("L'offre doit être en attente (PENDING) pour être publiée.");
        }

        // Enrichissement et publication (Spécification 2.A)
        offer.validateAndPublish(salary, remoteDays);
        jobOfferRepository.save(offer);
    }


}
