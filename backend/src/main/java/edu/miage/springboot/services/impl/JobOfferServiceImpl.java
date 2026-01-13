package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.JobOfferEntity;
import edu.miage.springboot.dao.repositories.JobOfferRepository;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.utils.mappers.JobOfferMapper;
import edu.miage.springboot.web.dtos.JobOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
    public JobOfferDTO updateStatus(Long id, String status) {
        var offer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        offer.setStatus(status);
        jobOfferRepository.save(offer);

        return jobOfferMapper.entityToDto(offer);
    }

    @Override
    public JobOfferDTO createJobOffer(JobOfferDTO jobOfferDTO) {
        // Conversion DTO -> Entity
        JobOfferEntity entity = jobOfferMapper.dtoToEntity(jobOfferDTO);
        // Sauvegarde
        JobOfferEntity savedEntity = jobOfferRepository.save(entity);
        // Retour DTO
        return jobOfferMapper.entityToDto(savedEntity);
    }

    @Override
    public JobOfferDTO updateJobOffer(Long id, JobOfferDTO jobOfferDTO) {
        JobOfferEntity existingOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // Mise à jour des champs (Idéalement, utilise le mapper ou des setters ici)
        existingOffer.setTitle(jobOfferDTO.getTitle());
        existingOffer.setDescription(jobOfferDTO.getDescription());
        existingOffer.setDeadline(jobOfferDTO.getDeadline());
        existingOffer.setLocation(jobOfferDTO.getLocation());
        existingOffer.setSalary(jobOfferDTO.getSalary());
        existingOffer.setStatus(jobOfferDTO.getStatus());

        return jobOfferMapper.entityToDto(jobOfferRepository.save(existingOffer));
    }

    @Override
    public void deleteJobOffer(Long id) {
        jobOfferRepository.deleteById(id);
    }

    // Méthode de recherche pour ton rôle spécifique
    @Override
    public List<JobOfferDTO> searchJobOffers(String keyword) {
        List<JobOfferEntity> entities = jobOfferRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        return jobOfferMapper.entitiesToDtos(entities);
    }


}
