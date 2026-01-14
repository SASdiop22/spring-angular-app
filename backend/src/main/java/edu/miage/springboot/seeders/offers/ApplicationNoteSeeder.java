package edu.miage.springboot.seeders.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationNoteEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.offers.ApplicationNoteRepository;
import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Order(6) // S'exécute après ApplicationSeeder(5)
public class ApplicationNoteSeeder implements CommandLineRunner {

    @Autowired private ApplicationNoteRepository noteRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (noteRepository.count() > 0) return;

        // 1. Récupération des acteurs (RH et Demandeur définis dans UserSeeder)
        UserEntity aliceRh = userRepository.findByUsername("alice.rh")
                .orElseThrow(() -> new RuntimeException("Alice RH non trouvée"));
        UserEntity dylanDemandeur = userRepository.findByUsername("dylan.demandeur")
                .orElseThrow(() -> new RuntimeException("Dylan Demandeur non trouvé"));

        // 2. Récupération des candidatures créées dans ApplicationSeeder
        // Note: On récupère par index ou par le nom du candidat pour être précis
        applicationRepository.findAll().forEach(app -> {

            if (app.getCandidate().getUser().getUsername().equals("jean.candidat")) {
                // Simulation d'un journal pour Jean
                createNote(app, aliceRh, "Qualification RH",
                        "Excellent premier contact. Jean est très motivé par le projet SI.", 2);

                createNote(app, dylanDemandeur, "Entretien Technique",
                        "Profil technique solide sur Spring Boot. Un peu juste sur Angular mais prêt à apprendre.", 1);
            }

            if (app.getCandidate().getUser().getUsername().equals("marie.candidat")) {
                // Simulation d'une note de vigilance pour Marie
                createNote(app, aliceRh, "Revue de dossier",
                        "Attention, Marie a une période de préavis de 3 mois. À négocier.", 5);
            }
        });

        System.out.println("===> ApplicationNoteSeeder : Journal de recrutement initialisé.");
    }

    private void createNote(ApplicationEntity app, UserEntity author, String step, String content, int daysAgo) {
        ApplicationNoteEntity note = new ApplicationNoteEntity();
        note.setApplication(app);
        note.setAuthor(author);
        note.setStepName(step);
        note.setContent(content);
        // On simule une date passée pour la chronologie
        note.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));
        noteRepository.save(note);
    }
}