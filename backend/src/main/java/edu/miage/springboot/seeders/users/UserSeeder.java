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

        UserRoleEntity roleCandidat = getOrCreateRole("ROLE_CANDIDAT");
        UserRoleEntity roleEmploye = getOrCreateRole("ROLE_EMPLOYE");
        UserRoleEntity roleRH = getOrCreateRole("ROLE_RH");
        UserRoleEntity roleAdmin = getOrCreateRole("ROLE_ADMIN");

        // Création des comptes utilisateurs de base
        createBaseUser("jean.candidat", "jean@test.com", "Jean", "Dupont", Set.of(roleCandidat), UserTypeEnum.CANDIDAT);
        createBaseUser("marie.candidat", "marie@test.com", "Marie", "Hihi", Set.of(roleCandidat), UserTypeEnum.CANDIDAT);
        createBaseUser("alice.rh", "rh@test.com", "Alice", "RH", Set.of(roleRH), UserTypeEnum.RH);
        createBaseUser("bob.admin", "admin@test.com", "Admin", "System", Set.of(roleAdmin), UserTypeEnum.ADMIN);
        createBaseUser("cathy.employe", "employe@test.com", "Cathy", "Employe", Set.of(roleEmploye), UserTypeEnum.EMPLOYE);
        createBaseUser("dylan.demandeur", "demandeur@test.com", "Dylan", "Demandeur", Set.of(roleEmploye), UserTypeEnum.EMPLOYE);
        //
    }

    private UserEntity createBaseUser(String username, String email, String prenom, String nom, Set<UserRoleEntity> roles, UserTypeEnum type) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123")); //
        user.setEmail(email);
        user.setPrenom(prenom);
        user.setNom(nom);
        user.setRoles(roles);
        user.setUserType(type);
        return userRepository.save(user);
    }

    private UserRoleEntity getOrCreateRole(String roleName) {
        return userRoleRepository.findByName(roleName).orElseGet(() -> userRoleRepository.save(new UserRoleEntity(roleName)));
    }
}