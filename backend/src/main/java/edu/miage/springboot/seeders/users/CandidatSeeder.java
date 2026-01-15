package edu.miage.springboot.seeders.users;

import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.dao.entities.users.CandidatEntity;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Order(3)
public class CandidatSeeder implements CommandLineRunner {
    @Autowired private CandidatRepository candidatRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (candidatRepository.count() > 0) return;

        // === CANDIDATS EXISTANTS (Scénarios) ===
        createCandidat(
            "jean.rgpd",
            "Lyon",
            "0612345678",
            LocalDateTime.now().minusYears(1).minusMonths(11),
            Arrays.asList("Python", "Machine Learning", "SQL", "Pandas")
        );

        createCandidat(
            "marie.hired",
            "Paris",
            "0623456789",
            LocalDateTime.now().minusMonths(1),
            Arrays.asList("Java", "Spring Boot", "Docker", "PostgreSQL")
        );

        createCandidat(
            "sophie.onboard",
            "Toulouse",
            "0634567890",
            LocalDateTime.now().minusMonths(2),
            Arrays.asList("React", "TypeScript", "CSS", "JavaScript")
        );

        createCandidat(
            "paul.rejet",
            "Bordeaux",
            "0645678901",
            LocalDateTime.now().minusDays(5),
            Arrays.asList("C++", "Linux", "Embedded Systems")
        );

        // === CANDIDATS SUPPLÉMENTAIRES (pour enrichir la base) ===
        createCandidat(
            "marie.dupont",
            "Paris",
            "0656789012",
            LocalDateTime.now().minusMonths(3),
            Arrays.asList("Java", "Spring", "Microservices", "AWS", "Docker")
        );

        createCandidat(
            "jean.martin",
            "Lyon",
            "0667890123",
            LocalDateTime.now().minusMonths(2),
            Arrays.asList("Python", "Django", "PostgreSQL", "Redis")
        );

        createCandidat(
            "claire.bernard",
            "Toulouse",
            "0678901234",
            LocalDateTime.now().minusMonths(1),
            Arrays.asList("React", "Node.js", "MongoDB", "GraphQL")
        );

        createCandidat(
            "pierre.durand",
            "Bordeaux",
            "0689012345",
            LocalDateTime.now().minusMonths(4),
            Arrays.asList("Angular", "TypeScript", "RxJS", "Material Design")
        );

        createCandidat(
            "alice.rousseau",
            "Paris",
            "0690123456",
            LocalDateTime.now().minusMonths(2),
            Arrays.asList("AWS", "Kubernetes", "Terraform", "CI/CD")
        );

        createCandidat(
            "bob.leblanc",
            "Lyon",
            "0601234567",
            LocalDateTime.now().minusMonths(1),
            Arrays.asList("Data Science", "Machine Learning", "TensorFlow", "Python")
        );

        createCandidat(
            "celine.moreau",
            "Toulouse",
            "0612345679",
            LocalDateTime.now().minusDays(20),
            Arrays.asList("Product Management", "Agile", "User Research")
        );

        createCandidat(
            "david.michel",
            "Bordeaux",
            "0623456790",
            LocalDateTime.now().minusMonths(3),
            Arrays.asList("DevOps", "Docker", "Jenkins", "GitLab")
        );

        System.out.println("✓ CandidatSeeder : " + candidatRepository.count() + " candidats créés");
    }

    private void createCandidat(
        String username,
        String ville,
        String telephone,
        LocalDateTime consentDate,
        java.util.List<String> skills
    ) {
        var userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            System.out.println("⚠️  Utilisateur non trouvé pour candidat : " + username + " (ignoré)");
            return;
        }

        UserEntity user = userOptional.get();
        CandidatEntity candidat = new CandidatEntity();
        candidat.setUser(user);
        candidat.setVille(ville);
        candidat.setTelephone(telephone);
        candidat.setConsentDate(consentDate);
        candidat.setArchived(false);

        // Ajouter les compétences
        if (skills != null) {
            skills.forEach(skill -> candidat.getSkills().add(skill));
        }

        candidatRepository.save(candidat);
    }
}