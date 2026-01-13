package edu.miage.springboot.services.impl;

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
}
