package edu.miage.springboot.security;

import edu.miage.springboot.dao.entities.JobOfferEntity;
import edu.miage.springboot.dao.repositories.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
public class SecurityService {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    /**
     * Vérifie si l'utilisateur actuellement authentifié est le créateur de l'offre.
     * Utilisé par @PreAuthorize dans le JobOfferController.
     */
    public boolean isOwner(Long jobOfferId) {
        // 1. Récupération de l'utilisateur authentifié depuis le contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String currentUsername = authentication.getName();

        // 2. Recherche de l'offre et comparaison avec le créateur
        Optional<JobOfferEntity> jobOffer = jobOfferRepository.findById(jobOfferId);

        return jobOffer.map(offer -> {
            // On remonte de l'offre -> l'employé créateur -> son compte utilisateur -> le username
            // Cela correspond à la structure définie dans JobOfferEntity (champ creator)
            if (offer.getCreator() != null && offer.getCreator().getUser() != null) {
                return offer.getCreator().getUser().getUsername().equals(currentUsername);
            }
            return false;
        }).orElse(false);
    }
}