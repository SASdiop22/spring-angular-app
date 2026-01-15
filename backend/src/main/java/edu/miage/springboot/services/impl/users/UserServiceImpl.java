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



}
