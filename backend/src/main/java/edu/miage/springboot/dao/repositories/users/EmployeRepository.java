package edu.miage.springboot.dao.repositories.users;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<EmployeEntity, Long> {

    // Trouver un employé par son email (via la relation avec User)
    Optional<EmployeEntity> findByUserEmail(String email);

    // Lister les employés par département (Utile pour la Spec 2.A)
    List<EmployeEntity> findByDepartement(String departement);

    // Trouver tous les employés qui sont habilités à être recruteurs
    List<EmployeEntity> findByDemandeurDePosteTrue();
    List<EmployeEntity> findByRhPrivilegeTrue();

    Optional<EmployeEntity> findByUser(UserEntity rhUser);

    // Spring va automatiquement faire la jointure avec UserEntity
    Optional<EmployeEntity> findByUserUsername(String username);

    // Optionnel : pratique si vous travaillez avec les IDs techniques
    Optional<EmployeEntity> findByUserId(Long userId);
}