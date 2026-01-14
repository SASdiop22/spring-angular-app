package edu.miage.springboot.seeders.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationStatusEnum;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Order(5) // S'exécute après UserSeeder(1) et JobOfferSeeder(2)
public class ApplicationSeeder implements CommandLineRunner {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (applicationRepository.count() > 0) return;

        // 1. Récupération des données nécessaires
        CandidatEntity jean = candidatRepository.findByUserUsername("jean.candidat")
                .orElseThrow(() -> new RuntimeException("Candidat jean.candidat non trouvé"));

        CandidatEntity marie = candidatRepository.findByUserUsername("marie.candidat")
                .orElseThrow(() -> new RuntimeException("Candidat marie.candidat non trouvé"));

        CandidatEntity cathy = candidatRepository.findByUserUsername("cathy.employe")
                .orElseThrow(() -> new RuntimeException("Candidat cathy.employe non trouvé"));

        JobOfferEntity offerCloud = jobOfferRepository.findAll().stream()
                .filter(o -> o.getTitle().contains("AWS"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Offre AWS non trouvée"));

        JobOfferEntity offerSI = jobOfferRepository.findAll().stream()
                .filter(o -> o.getTitle().contains("SI"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Offre SI non trouvée"));

        // --- CAS 1 : Candidature reçue (Nouveau flux) ---
        ApplicationEntity app1 = new ApplicationEntity();
        app1.setCandidate(jean);
        app1.setJob(offerCloud);
        app1.setCvUrl("https://storage.cloud/cv-jean.pdf");
        app1.setCoverLetter("Passionné par le Cloud et Docker.");
        app1.setCurrentStatus(ApplicationStatusEnum.RECEIVED);
        app1.setMatchingScore(85); // Score élevé
        applicationRepository.save(app1);

        // --- CAS 2 : Candidature en cours d'entretien (Planification) ---
        ApplicationEntity app2 = new ApplicationEntity();
        app2.setCandidate(marie);
        app2.setJob(offerCloud);
        app2.setCvUrl("https://storage.cloud/cv-sophie.pdf");
        app2.setCurrentStatus(ApplicationStatusEnum.INTERVIEW_PENDING);
        app2.setMeetingDate(LocalDateTime.now().plusDays(3).withHour(14).withMinute(0));
        app2.setMatchingScore(92);
        applicationRepository.save(app2);

        // --- CAS 3 : Test Spécification 5 (HIRED & Onboarding) ---
        // On simule un candidat déjà recruté pour vérifier les liens referent_employe_id
        ApplicationEntity app3 = new ApplicationEntity();
        app3.setCandidate(cathy);
        app3.setJob(offerSI);
        app3.setCvUrl("https://storage.cloud/cv-cathy.pdf");
        app3.setCurrentStatus(ApplicationStatusEnum.HIRED);

        // IMPORTANT : Quand un candidat est HIRED, l'offre doit passer en FILLED
        offerSI.setStatus(JobStatusEnum.FILLED);
        // Et on lie le candidat à son nouveau manager (le créateur de l'offre)
        jean.getUser().setReferentEmploye(offerSI.getCreator());

        jobOfferRepository.save(offerSI);
        applicationRepository.save(app3);

        System.out.println(">> ApplicationSeeder : 3 candidatures créées (RECEIVED, INTERVIEW, HIRED).");
    }
}