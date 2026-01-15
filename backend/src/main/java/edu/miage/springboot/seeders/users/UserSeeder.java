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
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) return;

        // Rôles
        UserRoleEntity rCandidat = getOrCreateRole("ROLE_CANDIDAT");
        UserRoleEntity rEmploye = getOrCreateRole("ROLE_EMPLOYE");
        UserRoleEntity rRh = getOrCreateRole("ROLE_RH");
        UserRoleEntity rAdmin = getOrCreateRole("ROLE_ADMIN");

        // --- ACTEURS GLOBAUX ---
        createUser("alice.rh", "ROLE_RH", UserTypeEnum.RH);
        createUser("bob.admin", "ROLE_ADMIN", UserTypeEnum.ADMIN);

        // --- ACTEURS SCÉNARIO 1 & 4 (Cycle de vie & Sécurité) ---
        createUser("cathy.employe", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);

        // --- ACTEURS SCÉNARIO 2 (RGPD) ---
        createUser("jean.rgpd", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);

        // --- ACTEURS SCÉNARIO 3 & 5 (Recrutement complet & Onboarding) ---
        createUser("marie.hired", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("sophie.onboard", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);

        // --- ACTEURS SCÉNARIO 6 & 7 (Journal & Rejets) ---
        createUser("dylan.demandeur", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("paul.rejet", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);

        // --- EMPLOYÉS SUPPLÉMENTAIRES (pour enrichir la base) ---
        createUser("emma.marketing", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("francois.finance", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("genevieve.ventes", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("henry.it", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("isabelle.rh", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("jerome.tech", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);
        createUser("kathleen.ops", "ROLE_EMPLOYE", UserTypeEnum.EMPLOYE);

        // --- CANDIDATS SUPPLÉMENTAIRES (pour enrichir la base) ---
        createUser("marie.dupont", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("jean.martin", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("claire.bernard", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("pierre.durand", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("alice.rousseau", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("bob.leblanc", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("celine.moreau", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);
        createUser("david.michel", "ROLE_CANDIDAT", UserTypeEnum.CANDIDAT);

        System.out.println("✓ UserSeeder : " + userRepository.count() + " utilisateurs créés");
    }

    private void createUser(String username, String roleName, UserTypeEnum type) {
        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode("password123"));
        u.setEmail(username + "@test.com");
        u.setUserType(type);
        u.setRoles(Set.of(userRoleRepository.findByName(roleName).get()));
        userRepository.save(u);
    }

    private UserRoleEntity getOrCreateRole(String roleName) {
        return userRoleRepository.findByName(roleName).orElseGet(() -> userRoleRepository.save(new UserRoleEntity(roleName)));
    }
}