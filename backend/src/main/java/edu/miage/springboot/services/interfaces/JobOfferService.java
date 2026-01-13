package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.JobOfferDTO;

import java.util.List;

public interface JobOfferService {
    List<JobOfferDTO> findAll();
    JobOfferDTO findById(Long id);
}
