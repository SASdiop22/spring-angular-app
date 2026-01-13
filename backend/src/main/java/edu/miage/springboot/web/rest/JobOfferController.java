package edu.miage.springboot.web.rest;

import edu.miage.springboot.dao.repositories.JobOfferRepository;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.web.dtos.JobOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/joboffers")
public class JobOfferController {
    @Autowired
    private JobOfferService jobOfferService;

    @GetMapping
    public List<JobOfferDTO> getAll(){
        return jobOfferService.findAll();
    }
    @GetMapping("/{id}")
    public JobOfferDTO getById(@PathVariable Long id){
        return jobOfferService.findById(id);
    }
    @PutMapping("/{id}/status")
    public JobOfferDTO updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return jobOfferService.updateStatus(id, status);
    }

}
