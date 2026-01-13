package edu.miage.springboot.dao.repositories;

import edu.miage.springboot.dao.entities.UserRoleEntity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
    public Optional<UserRoleEntity> findByName(String name);
}