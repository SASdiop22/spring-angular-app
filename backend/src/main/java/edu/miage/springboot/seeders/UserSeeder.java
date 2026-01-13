package edu.miage.springboot.seeders;

import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.entities.UserRoleEntity;
import edu.miage.springboot.dao.repositories.UserRepository;
import edu.miage.springboot.dao.repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Initialisation des Rôles
        UserRoleEntity userRole = getOrCreateRole("ROLE_USER");
        UserRoleEntity adminRole = getOrCreateRole("ROLE_ADMIN");
        UserRoleEntity rhRole = getOrCreateRole("ROLE_RH");

        // 2. Création d'un utilisateur standard (Candidat)
        if (userRepository.findByUsername("user").isEmpty()) {
            UserEntity user = new UserEntity();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setEmail("candidat@test.fr");
            user.setNom("Dupont");
            user.setPrenom("Jean");
            user.setTelephone("0601020304");
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            System.out.println(">> Utilisateur 'user' créé.");
        }

        // 3. Création d'un administrateur
        if (userRepository.findByUsername("admin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setEmail("admin@miage.fr");
            admin.setNom("Admin");
            admin.setPrenom("Système");
            admin.setRoles(Set.of(adminRole, userRole)); // Un admin a souvent aussi le rôle user
            userRepository.save(admin);
            System.out.println(">> Utilisateur 'admin' créé.");
        }

        if (userRepository.findByUsername("Paulin").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername("Paulin");
            admin.setPassword(passwordEncoder.encode("Paulin"));
            admin.setEmail("paulin@miage.fr");
            admin.setNom("Admin");
            admin.setPrenom("Système");
            admin.setRoles(Set.of(adminRole, userRole)); // Un admin a souvent aussi le rôle user
            userRepository.save(admin);
            System.out.println(">> Utilisateur 'Paulin' créé.");
        }

        // 4. Création d'un profil RH (pour tester vos spécifications de recrutement)
        if (userRepository.findByUsername("rh_test").isEmpty()) {
            UserEntity rh = new UserEntity();
            rh.setUsername("rh_test");
            rh.setPassword(passwordEncoder.encode("123456"));
            rh.setEmail("rh@entreprise.com");
            rh.setNom("Recruteur");
            rh.setPrenom("Alice");
            rh.setRoles(Set.of(rhRole));
            userRepository.save(rh);
            System.out.println(">> Utilisateur 'rh_test' créé.");
        }
    }

    /**
     * Méthode utilitaire pour éviter de créer des doublons de rôles
     */
    private UserRoleEntity getOrCreateRole(String roleName) {
        return userRoleRepository.findByName(roleName)
                .orElseGet(() -> userRoleRepository.save(new UserRoleEntity(roleName)));
    }
}