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
        UserRoleEntity roleCandidat = getOrCreateRole("ROLE_CANDIDAT");
        UserRoleEntity roleEmploye = getOrCreateRole("ROLE_EMPLOYE");
        UserRoleEntity roleAdmin = getOrCreateRole("ROLE_ADMIN");

        
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

    }

    private UserRoleEntity getOrCreateRole(String roleName) {
        return userRoleRepository.findByName(roleName)
                .orElseGet(() -> userRoleRepository.save(new UserRoleEntity(roleName)));
    }
}
