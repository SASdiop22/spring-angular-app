package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.*;
import edu.miage.springboot.dao.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandidatureServiceImpl {

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Postuler à une offre
     */
    @Transactional
    public CandidatureEntity postuler(Long jobId, UserEntity candidate, String cvUrl, String coverLetter) {

        JobOfferEntity job = jobOfferRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // 2. Vérification que l'offre est bien ouverte
        if (job.getStatus() != JobStatusEnum.OPEN) {
            throw new IllegalStateException("Cette offre n'est plus ouverte aux candidatures.");
        }

        CandidatureEntity candidature = new CandidatureEntity();
        candidature.setJob(job);
        candidature.setCandidate(candidate);
        candidature.setCvUrl(cvUrl);
        candidature.setCoverLetter(coverLetter);
        candidature.setCurrentStatus(CandidatureStatusEnum.RECEIVED);

        // 3. Calcul du Matching Score (Logique simplifiée basée sur les tags)
        candidature.setMatchingScore(calculerMatchingScore(job, candidate));

        return candidatureRepository.save(candidature);
    }

    /**
     * Finaliser un recrutement (Spécification 5 - Succès)
     */
    @Transactional
    public void recruterCandidat(Long candidatureId, UserEntity currentUser) {
        // Seul un RH peut valider l'embauche finale
        if (!currentUser.getEmployeProfile().isRhPrivilege()) {
            throw new AccessDeniedException("Action réservée au personnel RH.");
        }

        CandidatureEntity app = candidatureRepository.findById(candidatureId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        // 1. Mise à jour du statut de la candidature
        app.setCurrentStatus(CandidatureStatusEnum.HIRED);

        // 2. Clôture automatique de l'offre (Status FILLED)
        JobOfferEntity job = app.getJob();
        job.setStatus(JobStatusEnum.FILLED);

        // 3. Création du lien hiérarchique (Référent)
        UserEntity recrue = app.getCandidate();
        recrue.setReferentEmploye(job.getCreator());

        candidatureRepository.save(app);
        jobOfferRepository.save(job);
        userRepository.save(recrue);
    }

    /**
     * Logique de matching (Spécification 3.A)
     */
    private Integer calculerMatchingScore(JobOfferEntity job, UserEntity candidate) {
        // Logique à implémenter : comparaison entre job.getSkillsRequired() 
        // et les compétences présentes sur le profil du candidat.
        return 75; // Score d'exemple
    }
}