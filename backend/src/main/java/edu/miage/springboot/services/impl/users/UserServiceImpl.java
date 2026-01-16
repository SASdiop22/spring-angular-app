package edu.miage.springboot.services.impl.users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import edu.miage.springboot.dao.entities.users.UserTypeEnum;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.utils.mappers.UserMapper;
import edu.miage.springboot.web.dtos.users.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.dao.repositories.users.UserRoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmployeRepository employeRepository;
    
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
    public void finaliserEmbauche(UserEntity candidat, EmployeEntity referent) {
        if (candidat == null || referent == null) {
            throw new IllegalArgumentException("Le candidat et le référent doivent exister.");
        }

        // SPEC 5 : Établissement effectif du lien hiérarchique
        candidat.setReferentEmploye(referent);

        // Sauvegarde pour persister le champ referent_employe_id
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

    @Transactional
    public UserDTO assignReferent(Long candidatId, Long employeId) {
        // 1. On récupère l'USER du candidat (ex: Sophie, ID 6)
        UserEntity candidat = userRepository.findById(candidatId)
                .orElseThrow(() -> new EntityNotFoundException("Candidat non trouvé"));

        // 2. On récupère le PROFIL EMPLOYE du référent (ex: Alice, ID 1)
        // Note : On utilise findByUserId car Alice a l'ID User 1
        EmployeEntity referent = employeRepository.findByUserId(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Profil Employé référent non trouvé"));

        // 3. Liaison
        candidat.setReferentEmploye(referent);

        // 4. Sauvegarde et conversion finale
        return userMapper.toDto(userRepository.save(candidat));
    }

    /**
     * Supprime définitivement un utilisateur et toutes ses données associées
     * ONLY FOR ADMIN
     */
    @Transactional
    public void deleteUserPermanently(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        System.out.println("🗑️ Suppression définitive de l'utilisateur: " + user.getUsername() + " (ID: " + userId + ")");

        // Supprimer les profils associés (Candidat ou Employé)
        // Les relations en cascade doivent s'en charger
        if (user.getCandidatProfile() != null) {
            System.out.println("  - Suppression du profil candidat");
            // Les cascades s'en chargeront
        }

        if (user.getEmployeProfile() != null) {
            System.out.println("  - Suppression du profil employé");
            // Les cascades s'en chargeront
        }

        // Supprimer les rôles
        user.getRoles().clear();

        // Supprimer l'utilisateur lui-même
        userRepository.delete(user);

        System.out.println("✅ Utilisateur " + user.getUsername() + " supprimé définitivement");
    }
}
