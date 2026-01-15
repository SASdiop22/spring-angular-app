package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.users.CandidatEntity;

public interface MatchingService {
    /**
     * Calcule le score de correspondance entre une offre et un candidat.
     * @return Score entre 0 et 100
     */
    Integer calculateMatchScore(JobOfferEntity offer, CandidatEntity candidate);
}