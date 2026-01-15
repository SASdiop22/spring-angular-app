package edu.miage.springboot.services.impl.users;

import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.dao.repositories.users.UserRoleRepository;
import edu.miage.springboot.services.interfaces.EmployeService;
import edu.miage.springboot.utils.mappers.EmployeMapper;
import edu.miage.springboot.utils.mappers.UserMapper;
import edu.miage.springboot.web.dtos.users.CandidatDTO;
import edu.miage.springboot.web.dtos.users.EmployeDTO;
import edu.miage.springboot.web.dtos.users.UserDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeServiceImpl implements EmployeService {
    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private EmployeMapper employeMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public EmployeDTO findById(Long id) {
        return employeRepository.findById(id)
                .map(employeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Employé non trouvé"));
    }

    @Override
    public List<EmployeDTO> findAllRh() {
        // On filtre les employés ayant le privilège RH
        return employeMapper.toDtos(
                employeRepository.findAll().stream()
                        .filter(EmployeEntity::isRhPrivilege)
                        .toList()
        );
    }

    @Override
    public List<UserDTO> getRecruits(Long employeId) {
        EmployeEntity manager = employeRepository.findById(employeId)
                .orElseThrow(() -> new EntityNotFoundException("Manager non trouvé"));

        // On utilise la liste hiérarchique "subordinates" (Employe -> Employe)
        return manager.getSubordinates().stream()
                .map(emp -> userMapper.toDto(emp.getUser()))
                .toList();
    }

    @Override
    @Transactional
    public void updateRhPrivilege(Long userId, boolean isRh) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        EmployeEntity employe = user.getEmployeProfile();
        if (employe == null) {
            throw new IllegalStateException("Cet utilisateur n'est pas un employé et ne peut donc pas être RH.");
        }

        // Mise à jour du booléen dans le profil employé
        employe.setRhPrivilege(isRh);

        // Mise à jour du rôle de sécurité (Spec 1.A)
        if (isRh) {
            userRoleRepository.findByName("ROLE_RH")
                    .ifPresent(role -> user.getRoles().add(role));
        } else {
            user.getRoles().removeIf(role -> role.getName().equals("ROLE_RH"));
        }

        userRepository.save(user);
    }

    @Transactional
    public UserDTO assignRecruitmentReferent(Long candidatId, Long employeId) {
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

    // Pour le lien hiérarchique Manager/Subordonné (après embauche)
    @Transactional
    public void setManagerReferent(Long employeId, Long managerId) {
        if (employeId.equals(managerId)) {
            throw new IllegalArgumentException("Un employé ne peut pas être son propre référent.");
        }
        EmployeEntity employe = employeRepository.findById(employeId).orElseThrow();
        EmployeEntity manager = employeRepository.findById(managerId).orElseThrow();
        employe.setReferent(manager); // Utilise le champ referent dans EmployeEntity
        employeRepository.save(employe);
    }

    @Override
    public EmployeDTO findByUsername(String username) {
        EmployeEntity employe = employeRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable avec l'id : " + username));
        return employeMapper.toDto(employe);
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