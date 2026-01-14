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
@Order(5)
public class ApplicationSeeder implements CommandLineRunner {
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private JobOfferRepository jobOfferRepository;
    @Autowired private CandidatRepository candidatRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (applicationRepository.count() > 0) return;

        //Scénario 2
        CandidatEntity jean = candidatRepository.findByUserUsername("jean.rgpd").get();
        JobOfferEntity offer2 = jobOfferRepository.findAll().get(1); // Data Analyst
        ApplicationEntity appJean = new ApplicationEntity();
        appJean.setCandidate(jean);
        appJean.setJob(offer2);
        appJean.setCvUrl("https://storage.provider.com/cvs/jean_cv.pdf");
        appJean.setCurrentStatus(ApplicationStatusEnum.RECEIVED);
        applicationRepository.save(appJean);

        // Candidature pour Scénario 5 (Sophie va être recrutée)
        CandidatEntity sophie = candidatRepository.findByUserUsername("sophie.onboard").get();
        JobOfferEntity offer3 = jobOfferRepository.findAll().get(2); // Product Manager

        ApplicationEntity appS5 = new ApplicationEntity();
        appS5.setCandidate(sophie);
        appS5.setJob(offer3);
        appS5.setCvUrl("https://storage.provider.com/cvs/sophie_cv.pdf");
        appS5.setCurrentStatus(ApplicationStatusEnum.INTERVIEW_PENDING);
        applicationRepository.save(appS5);

    }
}