package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.offers.*;
import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.offers.*;
import edu.miage.springboot.dao.repositories.users.*;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.utils.mappers.ApplicationMapper;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

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

        // Spécification 3.A : Vérification RGPD (Consentement < 2 ans)
        if (candidate.getConsentDate().isBefore(LocalDateTime.now().minusYears(2))) {
            throw new RuntimeException("Consentement RGPD expiré. Veuillez renouveler votre profil.");
        }

        ApplicationEntity app = new ApplicationEntity();
        app.setJob(job);
        app.setCandidate(candidate);
        app.setCvUrl(cvUrl);
        app.setCoverLetter(coverLetter);
        app.setCurrentStatus(ApplicationStatusEnum.RECEIVED);

        return applicationMapper.toDto(applicationRepository.save(app));
    }

    @Override
    @Transactional
    public ApplicationDTO updateStatus(Long id, ApplicationStatusEnum newStatus, String reason) {
        // Note : Dans une version réelle, on passerait aussi meetingDate et rejectionReason en paramètres
        ApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        app.setCurrentStatus(newStatus);

        // --- SPÉCIFICATION 4.B : Gestion des rendez-vous et tests ---
        if (isStatusRequiringMeeting(newStatus)) {
            // Dans l'idéal, la date est reçue via un DTO de mise à jour
            if (app.getMeetingDate() == null) {
                app.setMeetingDate(LocalDateTime.now().plusDays(7)); // Date par défaut pour le test
            }
            // TODO: emailService.sendMeetingInvitation(app.getCandidate().getUser().getEmail(), app.getMeetingDate())
        }

        if (newStatus == ApplicationStatusEnum.REJECTED) {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException("Le motif de rejet est obligatoire.");
            }
            app.setRejectionReason(reason);

            // Simulation Notification (Spec 4.4)
            sendRejectionEmail(app.getCandidate().getUser().getEmail(), reason);
        }

        // --- SPÉCIFICATION 5 : Processus d'embauche (HIRED) ---
        // --- SPÉCIFICATION 5 : Processus d'embauche (HIRED) ---
        if (newStatus == ApplicationStatusEnum.HIRED) {
            JobOfferEntity job = app.getJob();
            job.setStatus(JobStatusEnum.FILLED);
            jobOfferRepository.save(job);

            UserEntity recruit = app.getCandidate().getUser();
            recruit.setUserType(UserTypeEnum.EMPLOYE);
            recruit.setReferentEmploye(job.getCreator()); // Correction du lien hiérarchique

            userRoleRepository.findByName("ROLE_EMPLOYE").ifPresent(role -> {
                recruit.getRoles().clear();
                recruit.getRoles().add(role);
            });

            // Création du profil Employé métier
            EmployeEntity newProfile = new EmployeEntity();
            newProfile.setUser(recruit);
            newProfile.setPoste(job.getTitle());
            newProfile.setDepartement(job.getDepartment());
            employeRepository.save(newProfile);

            // Mise à jour explicite du profil candidat
            CandidatEntity candidatProfile = app.getCandidate();
            candidatProfile.setArchived(true);
            candidatRepository.saveAndFlush(candidatProfile);

            // Sauvegarde finale de l'utilisateur avec ses nouveaux rôles et son référent
            recruit.setReferentEmploye(job.getCreator());
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

    private void sendRejectionEmail(String email, String reason) {
        // Logique d'envoi de mail (ou simple log pour le moment)
        System.out.println("E-MAIL AUTO envoyé à " + email + " : Votre candidature a été rejetée pour le motif suivant : " + reason);
    }

    private boolean isStatusRequiringMeeting(ApplicationStatusEnum status) {
        return status == ApplicationStatusEnum.INTERVIEW_PENDING ||
                status == ApplicationStatusEnum.TECHNICAL_TEST_PENDING ||
                status == ApplicationStatusEnum.OFFER_PENDING;
    }

    @Override
    public List<ApplicationDTO> findAll() {
        return applicationMapper.toDtos(applicationRepository.findAll());
    }

    @Override
    public List<ApplicationDTO> findByCandidateId(Long candidateId) {
        return applicationMapper.toDtos(applicationRepository.findByCandidateId(candidateId));
    }
}