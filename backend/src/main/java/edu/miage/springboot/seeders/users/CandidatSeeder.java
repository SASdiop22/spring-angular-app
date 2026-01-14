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
    @Autowired
    private CandidatRepository candidatRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (candidatRepository.count() > 0) return;

        UserEntity user = userRepository.findByUsername("jean.candidat").orElseThrow();
        CandidatEntity c = new CandidatEntity();
        c.setUser(user);
        //c.setFirstName("Jean");
        //c.setLastName("Dupont");
        c.setVille("Paris");
        c.setConsentDate(LocalDateTime.now().minusMonths(6)); // Conforme
        candidatRepository.save(c);

        user = userRepository.findByUsername("marie.candidat").orElseThrow();
        c = new CandidatEntity();
        c.setUser(user);
        //c.setFirstName("Marie");
        //c.setLastName("Hihi");
        c.setVille("Paris");
        c.setConsentDate(LocalDateTime.now().minusMonths(6)); // Conforme
        candidatRepository.save(c);

        user = userRepository.findByUsername("cathy.employe").orElseThrow();
        c = new CandidatEntity();
        c.setUser(user);
        c.setVille("Marseille");
        c.setArchived(true);
        c.setConsentDate(LocalDateTime.now().minusMonths(6)); // Conforme
        candidatRepository.save(c);
    }
}