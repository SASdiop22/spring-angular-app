package edu.miage.springboot.dao.repositories.users;

import edu.miage.springboot.dao.entities.users.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
}