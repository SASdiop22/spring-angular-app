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

        // Jean : Consentement ancien (pour tester le renouvellement du Scénario 2)
        createCand("jean.rgpd", LocalDateTime.now().minusYears(1).minusMonths(11));

        // Les autres : Consentement récent
        createCand("marie.hired", LocalDateTime.now().minusMonths(1));
        createCand("sophie.onboard", LocalDateTime.now().minusMonths(2));
        createCand("paul.rejet", LocalDateTime.now().minusDays(5));
    }

    // Dans CandidatSeeder.java, modifie la méthode createCand
    private void createCand(String username, LocalDateTime consentDate) {
        UserEntity u = userRepository.findByUsername(username).orElseThrow();
        CandidatEntity c = new CandidatEntity();
        c.setUser(u);
        c.setVille("Lyon");
        c.setConsentDate(consentDate);

        // --- AJOUT DES SKILLS ---
        if (username.equals("marie.hired")) {
            c.setSkills(Arrays.asList("Java", "Spring Boot", "PostgreSQL", "Angular"));
        } else if (username.equals("paul.rejet")) {
            c.setSkills(Arrays.asList("Python", "Django", "Docker")); // Profil non-Java pour tester les scores bas
        } else if (username.equals("sophie.onboard")) {
            c.setSkills(Arrays.asList("Product Management", "Agile", "Jira", "Figma"));
        } else if (username.equals("jean.rgpd")) {
            c.setSkills(Arrays.asList("Java", "Hibernate"));
        }

        candidatRepository.save(c);
    }
}