package edu.miage.springboot.seeders;

import edu.miage.springboot.dao.entities.EmployeEntity;
import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.entities.UserRoleEntity;
import edu.miage.springboot.dao.entities.UserTypeEnum;
import edu.miage.springboot.dao.repositories.EmployeRepository;
import edu.miage.springboot.dao.repositories.UserRepository;
import edu.miage.springboot.dao.repositories.UserRoleRepository;
import org.springframework.core.annotation.Order; // CORRECTION : Import Spring
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@Order(1) // S'assure que les utilisateurs existent avant les JobOffers
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Initialisation des Rôles
        UserRoleEntity userRole = getOrCreateRole("ROLE_EMPLOYE");
        UserRoleEntity adminRole = getOrCreateRole("ROLE_ADMIN");
        UserRoleEntity rhRole = getOrCreateRole("ROLE_RH");
        UserRoleEntity candidateRole = getOrCreateRole("ROLE_CANDIDATE");

        // 2. Création des Utilisateurs et de leurs profils Employés associés

        // --- UTILISATEUR : Paulin (ADMIN) ---
        createFullUser("Paulin", "Paulin", "paulin@miage.fr", "Paulin", "Hehe",
                Set.of(adminRole, userRole), UserTypeEnum.ADMIN, "Directeur Technique", "Informatique");

        // --- UTILISATEUR : Paulin (ADMIN) ---
        createFullUser("admin", "admin", "admin@miage.fr", "Système", "Admin",
                Set.of(adminRole, userRole), UserTypeEnum.ADMIN, "Directeur Technique", "Informatique");

        // --- UTILISATEUR : rh_test (RH) ---
        createFullUser("rh_test", "123456", "rh@entreprise.com", "Alice", "Recruteur",
                Set.of(rhRole), UserTypeEnum.RH, "Responsable RH", "Ressources Humaines");

        // --- UTILISATEUR : user (EMPLOYE standard) ---
        createFullUser("user", "123456", "candidat@test.fr", "Jean", "Dupont",
                Set.of(userRole), UserTypeEnum.EMPLOYE, "Analyste", "Comptabilite");
    }

    /**
     * Méthode privée pour centraliser la création User + Employe
     * et respecter toutes les contraintes du schéma.
     */
    private void createFullUser(String username, String password, String email, String prenom, String nom,
                                Set<UserRoleEntity> roles, UserTypeEnum type, String poste, String departement) {

        if (userRepository.findByUsername(username).isEmpty()) {
            // Création UserEntity
            UserEntity user = new UserEntity();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setPrenom(prenom);
            user.setNom(nom);
            user.setRoles(roles);
            user.setUserType(type);
            user = userRepository.save(user); // Sauvegarde pour générer l'ID

            // Création EmployeEntity (nécessaire car lié par @MapsId)
            EmployeEntity employe = new EmployeEntity();
            employe.setUser(user);
            employe.setPoste(poste); // OBLIGATOIRE selon EmployeEntity.java
            employe.setDepartement(departement); // OBLIGATOIRE selon EmployeEntity.java

            // Gestion des privilèges selon le type
            if (type == UserTypeEnum.ADMIN) employe.setAdminPrivilege(true);
            if (type == UserTypeEnum.RH) employe.setRhPrivilege(true);

            employeRepository.save(employe);
            System.out.println(">> Utilisateur et profil Employé créés pour : " + username);
        }
    }

    private UserRoleEntity getOrCreateRole(String roleName) {
        return userRoleRepository.findByName(roleName)
                .orElseGet(() -> userRoleRepository.save(new UserRoleEntity(roleName)));
    }
}