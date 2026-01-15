package edu.miage.springboot.services.interfaces;

import edu.miage.springboot.web.dtos.users.CandidatDTO;
import edu.miage.springboot.web.dtos.users.EmployeDTO;
import edu.miage.springboot.web.dtos.users.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EmployeService {
    // Lecture
    EmployeDTO findById(Long id);
    List<EmployeDTO> findAllRh();
    List<UserDTO> getRecruits(Long employeId); // Pour la spec 5 (succès)

    // Logique métier
    void updateRhPrivilege(Long userId, boolean isRh);
    UserDTO assignRecruitmentReferent(Long candidatId, Long employeId);

    EmployeDTO findByUsername(String username);

    public void setManagerReferent(Long employeId, Long managerId);

    List<EmployeDTO> findAll();

    // Évolution future (Spec 2.A)
    // List<JobOfferDTO> getJobOffersCreatedBy(Long employeId);
}