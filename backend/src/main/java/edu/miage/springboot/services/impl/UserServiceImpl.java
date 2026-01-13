package edu.miage.springboot.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import edu.miage.springboot.dao.entities.EmployeEntity;
import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.entities.UserTypeEnum;
import edu.miage.springboot.dao.repositories.UserRepository;
import edu.miage.springboot.dao.repositories.UserRoleRepository;
import jakarta.transaction.Transactional;

public class UserServiceImpl {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;
    
    public void checkUserExists(String username) {
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) { 
            // Ici, .isEmpty() est une méthode native de Optional de Java
            System.out.println("Aucun utilisateur trouvé avec ce pseudo.");
        }
    }

    // Non finalisé
    // Spec 3.A : Vérification RGPD (Données < 2 ans) 
    public boolean isConsentValid(UserEntity user) {
        return user.getCreatedAt().isAfter(LocalDateTime.now().minusYears(2));
    }

    // Spec 5 : Lier le candidat recruté à l'employé (Demandeur)
    @Transactional
    public void finaliserEmbauche(UserEntity candidat, EmployeEntity employe) {
        candidat.setReferentEmploye(employe);
        userRepository.save(candidat);
    }

    // Spec 1 : Récupérer les emplpoyés ayant posté des offres de travail
    public List<UserEntity> getDemandeurDePosteList() {
        // Logique pour filtrer les utilisateurs ayant le rôle EMPLOYE et isDemandeurDePoste=true
        return userRepository.findAll().stream()
            .filter(u -> u.getEmployeProfile() != null && u.getEmployeProfile().isDemandeurDePoste())
            .toList();
    }

    @Transactional
    public void setDemandeurDePosteStatus(Long userId, boolean status) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        EmployeEntity employe = user.getEmployeProfile();
        
        if (employe == null) {
            throw new RuntimeException("Cet utilisateur n'a pas de profil employé");
        }
        employe.setDemandeurDePoste(status);
        if (status) {
            userRoleRepository.findByName("ROLE_EMPLOYE")
                .ifPresent(role -> user.setupAsDemandeurDePoste(role));
        }else{
            userRoleRepository.findByName("ROLE_EMPLOYE")
                .ifPresent(role -> user.downgradeFromPrivilege(role));
        }
        userRepository.save(user);
    }

    @Transactional
    public void setAdminStatus(Long userId, boolean status) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        EmployeEntity employe = user.getEmployeProfile();
        
        if (employe == null) {
            throw new RuntimeException("Cet utilisateur n'a pas de profil employé");
        }
        employe.setAdminPrivilege(status);
        if (status) {
            userRoleRepository.findByName("ROLE_ADMIN")
                .ifPresent(role -> user.setupAsAdmin(role));
        }else{
            userRoleRepository.findByName("ROLE_ADMIN")
                .ifPresent(role -> user.downgradeFromPrivilege(role));
        }
        userRepository.save(user);
    }

    @Transactional
    public void setRhStatus(Long userId, boolean isRh) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        EmployeEntity employe = user.getEmployeProfile();
                if (employe == null) {
            throw new RuntimeException("Cet utilisateur n'a pas de profil employé");
        }
        employe.setRhPrivilege(isRh);

        if (isRh) {
            userRoleRepository.findByName("ROLE_RH")
                .ifPresent(role -> user.setupAsRhPrivilege(role));
        }else{
            userRoleRepository.findByName("ROLE_RH")
                .ifPresent(role -> user.downgradeFromPrivilege(role));
        }
        userRepository.save(user);
    }
}
