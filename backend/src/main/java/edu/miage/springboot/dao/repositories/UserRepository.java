package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.EmployeEntity;
import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.entities.UserTypeEnum;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    // Pour la gestion des accès (Security)
    Optional<UserEntity> findByEmail(String email);

    // Pour lier officiellement le candidat recruté à son employeur (Spec 5)
    List<UserEntity> findByReferentEmploye(EmployeEntity employe);

    List<UserEntity> findByUserType(UserTypeEnum userType);
}