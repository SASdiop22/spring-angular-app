package edu.miage.springboot.web.rest.users;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.web.dtos.users.EmployeDTO;
import edu.miage.springboot.web.dtos.users.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/employes")
public class EmployeController {

    @Autowired
    private EmployeRepository employeRepository;

    @GetMapping("/rh")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<EmployeDTO> getRHPersonnel() {
        return employeRepository.findAll().stream()
                .filter(EmployeEntity::isRhPrivilege)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeDTO> getEmployeById(@PathVariable Long id) {
        return employeRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/demandeurs")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAnyAuthority('ROLE_RH')")
    public List<EmployeDTO> getDemandeurs() {
        return employeRepository.findAll().stream()
                .filter(EmployeEntity::isDemandeurDePoste)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/recrues")
    public ResponseEntity<List<UserDTO>> getMyRecruits(@PathVariable Long id) {
        return employeRepository.findById(id).map(emp -> {
            List<UserDTO> recrues = emp.getRecruesLiees().stream()
                    .map(this::convertUserToDTO) // Utilise le mapper de User
                    .collect(Collectors.toList());
            return ResponseEntity.ok(recrues);
        }).orElse(ResponseEntity.notFound().build());
    }

    private EmployeDTO convertToDTO(EmployeEntity entity) {
        EmployeDTO dto = new EmployeDTO();
        dto.setId(entity.getId());
        dto.setPoste(entity.getPoste());
        dto.setDepartement(entity.getDepartement());
        dto.setTelephoneInterne(entity.getTelephoneInterne());
        dto.setDemandeurDePoste(entity.isDemandeurDePoste());
        dto.setRhPrivilege(entity.isRhPrivilege());
        dto.setAdminPrivilege(entity.isAdminPrivilege());
        if (entity.getUser() != null) {
            dto.setUsername(entity.getUser().getUsername());
            dto.setEmail(entity.getUser().getEmail());
            dto.setNomComplet(entity.getUser().getPrenom() + " " + entity.getUser().getNom());
        }
        return dto;
    }

    // Mapper simplifié pour User (pour les recrues)
    private UserDTO convertUserToDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        return dto;
    }
}