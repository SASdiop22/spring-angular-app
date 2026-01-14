package edu.miage.springboot.seeders.offers;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@Order(4)
public class JobOfferSeeder implements CommandLineRunner {
    @Autowired private JobOfferRepository jobOfferRepository;
    @Autowired private EmployeRepository employeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (jobOfferRepository.count() > 0) return;

        EmployeEntity cathy = employeRepository.findByUserUsername("cathy.employe").get();
        EmployeEntity dylan = employeRepository.findByUserUsername("dylan.demandeur").get();

        // Offre ID 1 : Pour Scénario 1 (S'arrête en DRAFT/PENDING)
        createOffer("Dev Java S1", cathy, JobStatusEnum.DRAFT);

        // Offre ID 2 : Pour Scénario 2 (Ouverte pour Jean)
        createOffer("Data Analyst S2", dylan, JobStatusEnum.OPEN);

        // Offre ID 3 : Pour Scénario 3 (Flux complet)
        createOffer("Product Manager S3", dylan, JobStatusEnum.OPEN);

        // Offre ID 4 : Pour Scénario 6 & 7 (Notes & Rejets)
        createOffer("Cloud Architect S6-7", dylan, JobStatusEnum.OPEN);
    }

    private void createOffer(String title, EmployeEntity creator, JobStatusEnum status) {
        JobOfferEntity o = new JobOfferEntity();
        o.setTitle(title);
        o.setCreator(creator);
        o.setStatus(status);
        o.setDepartment(creator.getDepartement());
        if(status == JobStatusEnum.OPEN) {
            o.setSalaryRange(50000.0);
            o.setRemoteDays(2);
        }
        jobOfferRepository.save(o);
    }
}


