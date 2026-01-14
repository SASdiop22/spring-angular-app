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
@Order(6) // S'exécute après ApplicationSeeder (5) [cite: 5]
public class ApplicationNoteSeeder implements CommandLineRunner {

    @Autowired private ApplicationNoteRepository noteRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (noteRepository.count() > 0) return;

        // 1. Récupération des acteurs RH et Demandeur (définis dans UserSeeder)
        UserEntity aliceRh = userRepository.findByUsername("alice.rh")
                .orElseThrow(() -> new RuntimeException("Alice RH non trouvée"));
        UserEntity dylanDemandeur = userRepository.findByUsername("dylan.demandeur")
                .orElseThrow(() -> new RuntimeException("Dylan Demandeur non trouvé"));

        // 2. Récupération des candidatures pour ajouter des notes
        applicationRepository.findAll().forEach(app -> {
            String candidateUsername = app.getCandidate().getUser().getUsername();

            // Correction : Utilisation de "jean.rgpd" au lieu de "jean.candidat" [cite: 3, 4]
            if (candidateUsername.equals("jean.rgpd")) {
                createNote(app, aliceRh, "Qualification RH",
                        "Excellent premier contact. Jean est très motivé par le projet SI.", 2);

                createNote(app, dylanDemandeur, "Entretien Technique",
                        "Profil technique solide sur Spring Boot. Un peu juste sur Angular mais prêt à apprendre.", 1);
            }

            // Correction : Utilisation de "marie.hired" au lieu de "marie.candidat" [cite: 3, 4]
            if (candidateUsername.equals("marie.hired")) {
                createNote(app, aliceRh, "Revue de dossier",
                        "Attention, Marie a une période de préavis de 3 mois. À négocier.", 5);
            }

            // Ajout d'une note pour Sophie (Scénario 5 d'onboarding)
            if (candidateUsername.equals("sophie.onboard")) {
                createNote(app, aliceRh, "Validation Finale",
                        "Validation du profil pour le poste de Product Manager. Lancement du processus d'onboarding.", 0);
            }
        });

        System.out.println("===> ApplicationNoteSeeder : Journal de recrutement initialisé avec les bons pseudos.");
    }

    private void createNote(ApplicationEntity app, UserEntity author, String step, String content, int daysAgo) {
        ApplicationNoteEntity note = new ApplicationNoteEntity();
        note.setApplication(app);
        note.setAuthor(author);
        note.setStepName(step);
        note.setContent(content);
        note.setCreatedAt(LocalDateTime.now().minusDays(daysAgo));
        noteRepository.save(note);
    }
}