package edu.miage.springboot.web.rest;

import edu.miage.springboot.dao.entities.JobStatusEnum;
import edu.miage.springboot.dao.repositories.JobOfferRepository;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.web.dtos.JobOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ROLE_RH', 'ROLE_ADMIN')")
    public ResponseEntity<JobOfferDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam JobStatusEnum status) {

        // Spring Boot convertit automatiquement le String en Enum si le nom correspond
        // Si la conversion échoue, une erreur 400 Bad Request sera renvoyée
        JobOfferDTO updatedOffer = jobOfferService.updateStatus(id, status);

        return ResponseEntity.ok(updatedOffer);
    }
    @GetMapping("/search")
    public List<JobOfferDTO> search(@RequestParam String keyword) {
        return jobOfferService.searchJobOffers(keyword);
    }

    @PostMapping
    public ResponseEntity<JobOfferDTO> create(@RequestBody JobOfferDTO jobOfferDTO) {
        return new ResponseEntity<>(jobOfferService.createJobOffer(jobOfferDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOfferDTO> update(@PathVariable Long id, @RequestBody JobOfferDTO jobOfferDTO) {
        return ResponseEntity.ok(jobOfferService.updateJobOffer(id, jobOfferDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobOfferService.deleteJobOffer(id);
        return ResponseEntity.noContent().build();
    }

}
