package edu.miage.springboot.seeders;

import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.entities.UserRoleEntity;
import edu.miage.springboot.dao.repositories.UserRepository;
import edu.miage.springboot.dao.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.util.Set;
@Component
public class UserSeeder implements CommandLineRunner {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public void run(String... args) throws Exception {

        // 1. Initialisation des rôles avec vérification d'existence
        UserRoleEntity roleCandidat = createRoleIfNotFound("ROLE_CANDIDAT");
        UserRoleEntity roleEmploye = createRoleIfNotFound("ROLE_EMPLOYE");
        UserRoleEntity roleAdmin = createRoleIfNotFound("ROLE_ADMIN");

        
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@uha.com");

            admin.setupAsEmploye(roleEmploye); 
            admin.getRoles().add(roleAdmin); // On ajoute le rôle ADMIN en plus
            
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("candidat_test").isEmpty()) {
            UserEntity candidat = new UserEntity();
            candidat.setUsername("candidat_test");
            candidat.setPassword(passwordEncoder.encode("password"));
            candidat.setEmail("candidat@uha.com");
            
            candidat.setupAsCandidat(roleCandidat);
            
            userRepository.save(candidat);
        }
        // 1. Création des rôles s'ils n'existent pas
        UserRoleEntity rhRole = createRoleIfNotFound("ROLE_RH");
        UserRoleEntity recruiterRole = createRoleIfNotFound("ROLE_RECRUITER");
        UserRoleEntity candidateRole = createRoleIfNotFound("ROLE_CANDIDATE");

        // 2. Création d'un utilisateur RH (Admin) de test
        createUserIfNotFound("admin_rh", "admin123", rhRole);

        // 3. Création d'un utilisateur Recruteur de test
        createUserIfNotFound("recruteur_test", "recruteur123", recruiterRole);

        // 4. Création d'un Candidat de test (pour que Pape puisse tester son portail)
        createUserIfNotFound("candidat_test", "candidat123", candidateRole);
    }

    private UserRoleEntity createRoleIfNotFound(String name) {
        return userRoleRepository.findByName(name).orElseGet(() -> {
            UserRoleEntity role = new UserRoleEntity();
            role.setName(name);
            return userRoleRepository.save(role);
        });
    }

    private void createUserIfNotFound(String username, String password, UserRoleEntity role) {
        if (userRepository.findByUsername(username) == null) {
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password)); // On encode toujours !
            user.setRoles(Set.of(role));
            userRepository.save(user);
        }
    }
}
