package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.*;
import edu.miage.springboot.dao.repositories.*;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.services.interfaces.AiMatchingService;
import edu.miage.springboot.web.dtos.ApplicationDTO;
import edu.miage.springboot.web.dtos.ai.MatchingResultDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private UserRepository userRepository;

    // 🔹 AJOUT IA (isolé, sans impact sur les autres services)
    @Autowired
    private AiMatchingService aiMatchingService;

    /**
     * Spécification 3.A : Dépôt de candidature et RGPD
     */
    @Override
    @Transactional
    public void apply(Long jobOfferId, Long candidateId, String cvUrl, String coverLetter) {

        UserEntity candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable"));

        // Vérification RGPD : consentement < 2 ans
        if (candidate.getCreatedAt().isBefore(LocalDateTime.now().minusYears(2))) {
            throw new IllegalStateException("Consentement RGPD expiré. Veuillez mettre à jour votre profil.");
        }

        JobOfferEntity job = jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        if (job.getStatus() != JobStatusEnum.OPEN) {
            throw new IllegalStateException("Cette offre n'est plus ouverte.");
        }

        ApplicationEntity app = new ApplicationEntity();
        app.setCandidate(candidate);
        app.setJob(job);
        app.setCvUrl(cvUrl);
        app.setCoverLetter(coverLetter);
        app.setCurrentStatus(ApplicationStatusEnum.RECEIVED);

        // ===============================
        // 🔹 IA MATCHING (intégré proprement)
        // ===============================

        // ⚠️ Pour l’instant : texte CV simulé (test local)
        // 👉 PLUS TARD : remplacé par extraction réelle depuis cvUrl
        String cvText = "Java Spring Boot developer with REST API and SQL experience";

        MatchingResultDTO result = aiMatchingService.matchCvWithJob(
                cvText,
                job.getDescription()
        );

        app.setMatchingScore(result.getMatchingScore());

        // ===============================
        applicationRepository.save(app);
    }

    /**
     * Spécification 5 : Clôture et Succès (Onboarding)
     */
    @Transactional
    public void finalizeRecruitment(Long applicationId, UserEntity currentUser) {

        if (!currentUser.getEmployeProfile().isRhPrivilege()) {
            throw new AccessDeniedException("Action réservée au personnel RH.");
        }

        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        // 1. Statut Candidature
        app.setCurrentStatus(ApplicationStatusEnum.HIRED);

        // 2. Clôture de l'offre
        JobOfferEntity job = app.getJob();
        job.setStatus(JobStatusEnum.FILLED);

        // 3. Onboarding : Lien hiérarchique avec le créateur de l'offre
        UserEntity recruit = app.getCandidate();
        recruit.setReferentEmploye(job.getCreator());

        applicationRepository.save(app);
        jobOfferRepository.save(job);
        userRepository.save(recruit);
    }

    @Override
    public List<ApplicationDTO> findAll() {
        return List.of();
    }
}
