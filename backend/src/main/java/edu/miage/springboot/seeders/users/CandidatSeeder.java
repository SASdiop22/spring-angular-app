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

    private void createCand(String username, LocalDateTime consentDate) {
        UserEntity u = userRepository.findByUsername(username).orElseThrow();
        CandidatEntity c = new CandidatEntity();
        c.setUser(u);
        c.setVille("Lyon");
        c.setConsentDate(consentDate);
        candidatRepository.save(c);
    }
}