package edu.miage.springboot.seeders;

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
@Order(4) // S'exécute après UserSeeder pour avoir les employés
public class JobOfferSeeder implements CommandLineRunner {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (jobOfferRepository.count() > 0) return;

        // 1. Récupération d'un employé pour simuler le "Demandeur"
        // Note: Assurez-vous que l'entité Employe est liée à l'User "rh_test" ou "admin"
        EmployeEntity demandeur = employeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun employé trouvé pour créer des offres."));

        // 2. Offre en DRAFT (Spécification 2.A)
        JobOfferEntity draftOffer = new JobOfferEntity();
        draftOffer.setTitle("Développeur Java Junior");
        draftOffer.setDescription("Poste en alternance pour projet innovant.");
        draftOffer.setDepartment("IT");
        draftOffer.setLocation("Paris");
        draftOffer.setDeadline(LocalDate.now().plusMonths(2));
        draftOffer.setCreator(demandeur);
        draftOffer.setStatus(JobStatusEnum.DRAFT);
        draftOffer.setSkillsRequired(Arrays.asList("Java", "Spring Boot"));
        jobOfferRepository.save(draftOffer);

        // 3. Offre en PENDING (En attente de validation RH)
        JobOfferEntity pendingOffer = new JobOfferEntity();
        pendingOffer.setTitle("Chef de Projet SI");
        pendingOffer.setDescription("Pilotage de la transformation digitale.");
        pendingOffer.setDepartment("SI");
        pendingOffer.setCreator(demandeur);
        pendingOffer.setStatus(JobStatusEnum.PENDING);
        pendingOffer.setSkillsRequired(Arrays.asList("Agile", "Governance"));
        jobOfferRepository.save(pendingOffer);

        // 4. Offre OPEN (Publiée avec salaire et télétravail - Spécification 2.A/B)
        JobOfferEntity openOffer = new JobOfferEntity();
        openOffer.setTitle("Expert Cloud AWS");
        openOffer.setDescription("Expertise sur l'infrastructure scalable.");
        openOffer.setDepartment("IT");
        openOffer.setCreator(demandeur);
        openOffer.setSkillsRequired(Arrays.asList("AWS", "Terraform", "Docker"));
        // Simulation de l'enrichissement RH
        openOffer.validateAndPublish(65000.0, 3);
        jobOfferRepository.save(openOffer);

        System.out.println(">> JobOfferSeeder : Offres de test (DRAFT, PENDING, OPEN) créées.");
    }
}