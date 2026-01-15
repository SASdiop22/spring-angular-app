package edu.miage.springboot.web.rest.users;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.services.interfaces.EmployeService;
import edu.miage.springboot.utils.mappers.EmployeMapper;
import edu.miage.springboot.utils.mappers.UserMapper;
import edu.miage.springboot.web.dtos.users.CandidatDTO;
import edu.miage.springboot.web.dtos.users.EmployeDTO;
import edu.miage.springboot.web.dtos.users.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/employes")
public class EmployeController {

    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private EmployeMapper employeMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmployeService employeService;

    @GetMapping("/rh")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<EmployeDTO> getRHPersonnel() {
        return employeService.findAllRh();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeDTO> getEmployeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.findById(id));
    }

    @GetMapping("/demandeurs")
    @PreAuthorize("@securityService.hasPrivilegedRole()")
    public List<EmployeDTO> getDemandeurs() {
        List<EmployeEntity> demList = employeRepository.findAll().stream()
                .filter(EmployeEntity::isDemandeurDePoste)
                .collect(Collectors.toList());

        return employeMapper.toDtos(demList);
    }

    @GetMapping("/{id}/recrues")
    public ResponseEntity<List<UserDTO>> getMyRecruits(@PathVariable Long id) {
        return ResponseEntity.ok(employeService.getRecruits(id));

    }

    @GetMapping("/me")
    @PreAuthorize("@securityService.isEmployeAnyKind()")
    public ResponseEntity<EmployeDTO> getCurrentEmploye(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(employeService.findByUsername(userDetails.getUsername()));
    }

    /**
     * Spécification 5 : Assigner un référent (manager) à un employé.
     * Accessible par RH ou Admin.
     */
    @PatchMapping("/{id}/referent")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RH')")
    public ResponseEntity<Void> updateReferent(
            @PathVariable Long id,
            @RequestParam Long referentId) {
        employeService.setManagerReferent(id, referentId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{userId}/rh-privilege")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> toggleRhPrivilege(
            @PathVariable Long userId,
            @RequestParam boolean status) {
        employeService.updateRhPrivilege(userId, status);
        return ResponseEntity.ok().build();
    }

}