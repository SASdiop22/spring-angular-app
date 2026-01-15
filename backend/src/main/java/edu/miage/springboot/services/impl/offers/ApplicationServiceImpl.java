package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.AiAnalysisResultEntity;
import edu.miage.springboot.dao.entities.offers.*;
import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.AiAnalysisResultRepository;
import edu.miage.springboot.dao.repositories.offers.*;
import edu.miage.springboot.dao.repositories.users.*;
import edu.miage.springboot.services.impl.users.UserServiceImpl;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.services.interfaces.AiMatchingService; // Version B
import edu.miage.springboot.utils.mappers.ApplicationMapper;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import edu.miage.springboot.web.dtos.ai.MatchingResultDTO; // Version B
import edu.miage.springboot.web.dtos.offers.ApplicationStatusUpdateDTO;
import edu.miage.springboot.web.dtos.users.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.antlr.v4.runtime.misc.LogManager;
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
    @Autowired
    private AiAnalysisResultRepository aiAnalysisResultRepository;
    @Autowired private UserServiceImpl userService;

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
        try {
            // On construit un texte simple pour l'analyse
            String textToAnalyze = "CANDIDATE LETTER: " + coverLetter;
            MatchingResultDTO aiResult = aiMatchingService.matchCvWithJob(textToAnalyze, job.getDescription());

            // On stocke le score
            app.setMatchingScore(aiResult.getMatchingScore());

            // Appel au service IA injecté

            AiAnalysisResultEntity detail = new AiAnalysisResultEntity();
            detail.setApplicationId(app.getId());
            detail.setJobOfferId(job.getId());
            detail.setMatchingScore(aiResult.getMatchingScore());
            detail.setStrengths(String.join(", ", aiResult.getStrengths()));
            detail.setMissingSkills(String.join(", ", aiResult.getMissingSkills()));
            detail.setRecommendation(aiResult.getRecommendation());


            aiAnalysisResultRepository.save(detail);

            // Mise à jour de l'entité avec le score retourné par Llama3
            app.setMatchingScore(aiResult.getMatchingScore());

            // Optionnel : Vous pourriez loguer la recommandation de l'IA ici
            System.out.println("IA Recommendation: " + aiResult.getRecommendation());

        } catch (Exception e) {
            // En cas d'échec de l'IA (ex: Ollama hors ligne), on met un score par défaut
            // pour ne pas bloquer la candidature technique
            app.setMatchingScore(0);
            System.err.println("IA Matching failed: " + e.getMessage());
        }

        return applicationMapper.toDto(applicationRepository.save(app));
    }

    @Override
    @Transactional
    public ApplicationDTO updateStatus(Long id, ApplicationStatusUpdateDTO updateDto) {
        ApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        ApplicationStatusEnum newStatus = updateDto.getStatus();

        // --- SPÉCIFICATION 4.4 : Motif de rejet obligatoire ---
        if (newStatus == ApplicationStatusEnum.REJECTED) {
            if (updateDto.getReason() == null || updateDto.getReason().trim().isEmpty()) {
                throw new IllegalArgumentException("Le motif de rejet est obligatoire.");
            }
            app.setRejectionReason(updateDto.getReason());
        }

        // --- SPÉCIFICATION 4.2 : Données logistiques obligatoires ---
        if (isStatusRequiringMeeting(newStatus)) {
            if (updateDto.getMeetingDate() == null || updateDto.getMeetingLocation() == null) {
                throw new IllegalArgumentException("La date, l'heure et le lieu sont obligatoires pour fixer un entretien.");
            }
            app.setMeetingDate(updateDto.getMeetingDate());
            app.setMeetingLocation(updateDto.getMeetingLocation());
            // Ici, vous pourriez déclencher l'envoi d'email automatique avec ces détails
        }

        app.setCurrentStatus(newStatus);

        // --- SPÉCIFICATION 5 : Processus d'embauche (HIRED) ---
        if (newStatus == ApplicationStatusEnum.HIRED) {
            JobOfferEntity job = app.getJob();
            UserEntity recruit = app.getCandidate().getUser();

            // 1. Établissement du lien hiérarchique (Correction majeure) [cite: 5, 7]
            if (job.getCreator() != null) {
                recruit.setReferentEmploye(job.getCreator()); // Fixe le referent_employe_id en base
            }

            // 2. Mutation du profil
            recruit.setUserType(UserTypeEnum.EMPLOYE);
            userRoleRepository.findByName("ROLE_EMPLOYE").ifPresent(role -> {
                recruit.getRoles().clear();
                recruit.getRoles().add(role);
            });

            // 3. Mise à jour de l'offre
            job.setStatus(JobStatusEnum.FILLED);
            jobOfferRepository.save(job);

            // 4. Création du profil Employé [cite: 1, 7]
            EmployeEntity newProfile = new EmployeEntity();
            newProfile.setUser(recruit);
            newProfile.setPoste(job.getTitle());
            newProfile.setDepartement(job.getDepartment());
            employeRepository.save(newProfile);

            // 5. Archivage du candidat
            CandidatEntity candidatProfile = app.getCandidate();
            candidatProfile.setArchived(true);
            candidatRepository.save(candidatProfile);

            // 6. Sauvegarde forcée de l'utilisateur pour que le Mapper voit le referentId
            userRepository.saveAndFlush(recruit);
        }

        // Sauvegarde et retour du DTO rafraîchi
        ApplicationEntity savedApp = applicationRepository.saveAndFlush(app);
        return applicationMapper.toDto(savedApp);
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