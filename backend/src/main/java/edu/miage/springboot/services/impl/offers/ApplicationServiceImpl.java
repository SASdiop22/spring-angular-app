package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.utils.mappers.ApplicationMapper;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private CandidatRepository candidatRepository;

    @Override
    @Transactional
    public ApplicationDTO apply(Long jobOfferId, Long candidateId, String cvUrl, String coverLetter) {
        // 1. Spécification 3.A : Vérification RGPD (Consentement < 2 ans)
        // On utilise la méthode optimisée du repository pour éviter de charger toute l'entité si invalide
        LocalDateTime threshold = LocalDateTime.now().minusYears(2);
        if (!candidatRepository.isConsentValid(candidateId, threshold)) {
            throw new IllegalStateException("Consentement RGPD invalide ou expiré (plus de 2 ans).");
        }

        // 2. Récupération des entités liées
        // Nous avons besoin de l'objet CandidatEntity complet pour la persistance de l'application
        CandidatEntity candidate = candidatRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Profil candidat introuvable"));

        JobOfferEntity job = jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable"));

        // 3. Création de la candidature
        ApplicationEntity app = new ApplicationEntity();
        app.setCandidate(candidate); // Utilise maintenant le CandidatEntity (ID partagé avec User)
        app.setJob(job);
        app.setCvUrl(cvUrl);
        app.setCoverLetter(coverLetter);
        app.setCurrentStatus(ApplicationStatusEnum.RECEIVED);

        // La date de création est gérée par le @PrePersist dans ApplicationEntity
        ApplicationEntity savedApp = applicationRepository.save(app);
        return applicationMapper.toDto(savedApp);
    }

    @Override
    @Transactional
    public ApplicationDTO updateStatus(Long id, ApplicationStatusEnum status) {
        ApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidature introuvable"));

        app.setCurrentStatus(status);

        // SPEC 5 : Si le candidat est marqué comme RECRUTÉ (HIRED)
        if (status == ApplicationStatusEnum.HIRED) {
            // 1. Clôturer l'offre automatiquement
            JobOfferEntity job = app.getJob();
            job.setStatus(JobStatusEnum.FILLED);
            jobOfferRepository.save(job);

            // 2. Onboarding : Le candidat est rattaché au créateur de l'offre (Manager)
            UserEntity recruit = app.getCandidate().getUser();
            recruit.setReferentEmploye(job.getCreator());
            userRepository.save(recruit);
        }

        return applicationMapper.toDto(applicationRepository.save(app));
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