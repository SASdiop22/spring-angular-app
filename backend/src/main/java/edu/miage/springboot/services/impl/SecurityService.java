package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

// Ajoutez l'import : import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;

@Service("securityService")
public class SecurityService {
    @Autowired private CandidatRepository candidatRepository;
    @Autowired private JobOfferRepository jobOfferRepository; // Ajouter ceci

    // Pour les candidats
    public boolean isOwner(Long candidateId) {
        String username = getConnectedUsername();
        return candidatRepository.findById(candidateId)
                .map(c -> c.getUser().getUsername().equals(username))
                .orElse(false);
    }

    // NOUVEAU : Pour les offres d'emploi
    public boolean isJobOfferOwner(Long jobOfferId) {
        String username = getConnectedUsername();
        return jobOfferRepository.findById(jobOfferId)
                .map(job -> job.getCreator().getUser().getUsername().equals(username))
                .orElse(false);
    }

    private String getConnectedUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();
    }
}