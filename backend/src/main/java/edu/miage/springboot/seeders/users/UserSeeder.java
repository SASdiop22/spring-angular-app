package edu.miage.springboot.seeders.users;

import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Order(1)
public class UserSeeder implements CommandLineRunner {
    @Autowired private UserRepository userRepository;
    @Autowired private UserRoleRepository userRoleRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        // Rôles (SANS le préfixe ROLE_ - celui-ci sera ajouté par AuthUserDetails)
        getOrCreateRole("CANDIDAT");
        getOrCreateRole("EMPLOYE");
        getOrCreateRole("RH");
        getOrCreateRole("ADMIN");

        // --- ACTEURS GLOBAUX ---
        createUser("alice.rh", "RH", UserTypeEnum.RH);
        createUser("bob.admin", "ADMIN", UserTypeEnum.ADMIN);

        // --- ACTEURS SCÉNARIO 1 & 4 (Cycle de vie & Sécurité) ---
        createUser("cathy.employe", "EMPLOYE", UserTypeEnum.EMPLOYE);

        // --- ACTEURS SCÉNARIO 2 (RGPD) ---
        createUser("jean.rgpd", "CANDIDAT", UserTypeEnum.CANDIDAT);

        // --- ACTEURS SCÉNARIO 3 & 5 (Recrutement complet & Onboarding) ---
        createUser("marie.hired", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("sophie.onboard", "CANDIDAT", UserTypeEnum.CANDIDAT);

        // --- ACTEURS SCÉNARIO 6 & 7 (Journal & Rejets) ---
        createUser("dylan.demandeur", "EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("paul.rejet", "CANDIDAT", UserTypeEnum.CANDIDAT);

        // --- EMPLOYÉS SUPPLÉMENTAIRES (pour enrichir la base) ---
        createUser("emma.marketing", "EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("francois.finance", "EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("genevieve.ventes", "EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("henry.it", "EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("isabelle.rh", "EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("jerome.tech", "EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("kathleen.ops", "EMPLOYE", UserTypeEnum.EMPLOYE);

        // --- CANDIDATS SUPPLÉMENTAIRES (pour enrichir la base) ---
        createUser("marie.dupont", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("jean.martin", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("claire.bernard", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("pierre.durand", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("alice.rousseau", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("bob.leblanc", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("celine.moreau", "CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("david.michel", "CANDIDAT", UserTypeEnum.CANDIDAT);

        System.out.println("✓ UserSeeder : " + userRepository.count() + " utilisateurs créés");
    }

    private void createUser(String username, String roleName, UserTypeEnum type) {
        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode("password123"));
        u.setEmail(username + "@test.com");
        u.setUserType(type);
        u.setRoles(Set.of(userRoleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Rôle non trouvé: " + roleName))));
        userRepository.save(u);
    }

    private UserRoleEntity getOrCreateRole(String roleName) {
        return userRoleRepository.findByName(roleName).orElseGet(() -> userRoleRepository.save(new UserRoleEntity(roleName)));
    }
}