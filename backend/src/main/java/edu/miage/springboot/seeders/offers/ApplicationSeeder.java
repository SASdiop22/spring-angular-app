package edu.miage.springboot.seeders.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import edu.miage.springboot.services.impl.offers.matching.MatchingServiceImpl; // Import du service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Order(5)
public class ApplicationSeeder implements CommandLineRunner {

    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private JobOfferRepository jobOfferRepository;
    @Autowired private CandidatRepository candidatRepository;

    // Injection du service de matching pour peupler les scores
    @Autowired private MatchingServiceImpl matchingService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (applicationRepository.count() > 0) return;

        List<JobOfferEntity> offers = jobOfferRepository.findAll();
        if (offers.isEmpty()) return;

        // --- Scénario 1 : Marie sur l'offre Dev Java ---
        // On s'attend à un score élevé car Marie a les skills Java
        CandidatEntity marie = candidatRepository.findByUserUsername("marie.hired")
                .orElseThrow(() -> new RuntimeException("Candidat marie.hired non trouvé"));
        JobOfferEntity offerJava = offers.get(0);

        createApplication(marie, offerJava, "https://storage.com/marie_cv.pdf", ApplicationStatusEnum.RECEIVED);

        // --- Scénario 2 : Jean sur l'offre Data Analyst ---
        CandidatEntity jean = candidatRepository.findByUserUsername("jean.rgpd")
                .orElseThrow(() -> new RuntimeException("Candidat jean.rgpd non trouvé"));
        JobOfferEntity offerData = offers.size() > 1 ? offers.get(1) : offerJava;

        createApplication(jean, offerData, "https://storage.com/jean_cv.pdf", ApplicationStatusEnum.RECEIVED);

        // --- Scénario 3 : Sophie sur l'offre Product Manager ---
        CandidatEntity sophie = candidatRepository.findByUserUsername("sophie.onboard")
                .orElseThrow(() -> new RuntimeException("Candidat sophie.onboard non trouvé"));
        JobOfferEntity offerPm = offers.size() > 2 ? offers.get(2) : offerJava;

        createApplication(sophie, offerPm, "https://storage.com/sophie_cv.pdf", ApplicationStatusEnum.RECEIVED);

        // --- Scénario 4 : Paul (Profil Python) sur l'offre Dev Java ---
        // On s'attend à un score de 0% (ou très bas) pour tester le filtrage
        CandidatEntity paul = candidatRepository.findByUserUsername("paul.rejet")
                .orElseThrow(() -> new RuntimeException("Candidat paul.rejet non trouvé"));

        createApplication(paul, offerJava, "http://storage.com/paul_cv.pdf", ApplicationStatusEnum.RECEIVED);

        System.out.println("===> ApplicationSeeder : Candidatures initialisées avec calcul automatique des scores de matching.");
    }

    /**
     * Méthode utilitaire pour créer une candidature avec calcul de score intégré
     */
    private void createApplication(CandidatEntity candidate, JobOfferEntity job, String cvUrl, ApplicationStatusEnum status) {
        ApplicationEntity app = new ApplicationEntity();
        app.setCandidate(candidate);
        app.setJob(job);
        app.setCvUrl(cvUrl);
        app.setCreatedAt(LocalDateTime.now());
        app.setCurrentStatus(status);

        // Calcul dynamique du score via le service de matching
        Integer score = matchingService.calculateMatchScore(job, candidate);
        app.setMatchingScore(score);

        applicationRepository.save(app);
    }
}