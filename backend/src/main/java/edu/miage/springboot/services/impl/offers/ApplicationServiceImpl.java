package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.offers.*;
import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.offers.*;
import edu.miage.springboot.dao.repositories.users.*;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.services.interfaces.AiMatchingService; // Version B
import edu.miage.springboot.utils.mappers.ApplicationMapper;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import edu.miage.springboot.web.dtos.ai.MatchingResultDTO; // Version B
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private JobOfferRepository jobOfferRepository;
    @Autowired private CandidatRepository candidatRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeRepository employeRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private ApplicationMapper applicationMapper;

    // 🔹 AJOUT IA (Version B)
    @Autowired private AiMatchingService aiMatchingService;

    @Override
    @Transactional
    public ApplicationDTO apply(Long jobOfferId, Long candidateId, String cvUrl, String coverLetter) {
        if (applicationRepository.existsByJobIdAndCandidateId(jobOfferId, candidateId)) {
            throw new IllegalStateException("Vous avez déjà postulé à cette offre.");
        }

        JobOfferEntity job = jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));
        CandidatEntity candidate = candidatRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

        // Spécification 3.A : Vérification RGPD (Utilisation de la méthode entity)
        if (!candidate.isRgpdCompliant()) {
            throw new RuntimeException("Consentement RGPD expiré. Veuillez renouveler votre profil.");
        }

        ApplicationEntity app = new ApplicationEntity();
        app.setJob(job);
        app.setCandidate(candidate);
        app.setCvUrl(cvUrl);
        app.setCoverLetter(coverLetter);
        app.setCurrentStatus(ApplicationStatusEnum.RECEIVED);

        // 🔹 INTEGRATION IA MATCHING (Version B)
        // Simulation de l'extraction de texte du CV (à lier à votre service de parsing plus tard)
        String cvText = "Compétences extraites du CV de " + candidate.getUser().getUsername();
        MatchingResultDTO result = aiMatchingService.matchCvWithJob(cvText, job.getDescription());
        app.setMatchingScore(result.getMatchingScore());

        return applicationMapper.toDto(applicationRepository.save(app));
    }

    @Override
    @Transactional
    public ApplicationDTO updateStatus(Long id, ApplicationStatusEnum newStatus, String reason) {
        ApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        app.setCurrentStatus(newStatus);

        // --- SPÉCIFICATION 4.B : Gestion des rendez-vous ---
        if (isStatusRequiringMeeting(newStatus)) {
            if (app.getMeetingDate() == null) {
                app.setMeetingDate(LocalDateTime.now().plusDays(7));
            }
        }

        if (newStatus == ApplicationStatusEnum.REJECTED) {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Le motif de rejet est obligatoire.");
            }
            app.setRejectionReason(reason);
            sendRejectionEmail(app.getCandidate().getUser().getEmail(), reason);
        }

        // --- SPÉCIFICATION 5 : Processus d'embauche (HIRED) ---
        if (newStatus == ApplicationStatusEnum.HIRED) {
            JobOfferEntity job = app.getJob();
            job.setStatus(JobStatusEnum.FILLED);
            jobOfferRepository.save(job);

            UserEntity recruit = app.getCandidate().getUser();
            recruit.setUserType(UserTypeEnum.EMPLOYE);
            recruit.setReferentEmploye(job.getCreator()); // Lien Manager

            userRoleRepository.findByName("ROLE_EMPLOYE").ifPresent(role -> {
                recruit.getRoles().clear();
                recruit.getRoles().add(role);
            });

            // Création du profil Employé métier (Spécification 5)
            EmployeEntity newProfile = new EmployeEntity();
            newProfile.setUser(recruit);
            newProfile.setPoste(job.getTitle());
            newProfile.setDepartement(job.getDepartment());
            employeRepository.save(newProfile);

            // Archivage et synchronisation forcée pour les tests (Correction de la Version A)
            CandidatEntity candidatProfile = app.getCandidate();
            candidatProfile.setArchived(true);
            candidatRepository.saveAndFlush(candidatProfile);

            userRepository.saveAndFlush(recruit);
            app.setCandidate(candidatProfile);
        }

        return applicationMapper.toDto(applicationRepository.save(app));
    }

    @Override
    public ApplicationDTO findById(Long id) {
        return applicationRepository.findById(id)
                .map(applicationMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable avec l'id : " + id));
    }

    @Override
    public List<ApplicationDTO> findAll() {
        return applicationMapper.toDtos(applicationRepository.findAll());
    }

    @Override
    public List<ApplicationDTO> findByCandidateId(Long candidateId) {
        return applicationMapper.toDtos(applicationRepository.findByCandidateId(candidateId));
    }

    private void sendRejectionEmail(String email, String reason) {
        System.out.println("E-MAIL AUTO : " + email + " rejeté pour : " + reason);
    }

    private boolean isStatusRequiringMeeting(ApplicationStatusEnum status) {
        return status == ApplicationStatusEnum.INTERVIEW_PENDING ||
                status == ApplicationStatusEnum.TECHNICAL_TEST_PENDING ||
                status == ApplicationStatusEnum.OFFER_PENDING;
    }
}