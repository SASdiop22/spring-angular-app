package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    public Optional<UserRoleEntity> findByName(String name);
}