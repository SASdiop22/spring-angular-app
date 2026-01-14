package edu.miage.springboot.dao.repositories.users;

import edu.miage.springboot.dao.entities.users.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    Optional<UserRoleEntity> findByName(String roleCandidate);
}