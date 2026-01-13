package edu.miage.springboot.dao.repositories;
import edu.miage.springboot.dao.entities.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long>{
}
