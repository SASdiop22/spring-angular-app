package edu.miage.springboot.web.rest.users;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.services.impl.users.UserServiceImpl;
import edu.miage.springboot.web.dtos.users.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_RH') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/rgpd-check")
    public ResponseEntity<Boolean> isRgpdValid(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            boolean isValid = user.getCreatedAt().isAfter(LocalDateTime.now().minusYears(2));
            return ResponseEntity.ok(isValid);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{candidatId}/assign-referent/{employeId}")
    @PreAuthorize("hasAuthority('ROLE_RH') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> assignReferent(@PathVariable Long candidatId, @PathVariable Long employeId) {
        // 1. Récupérer le candidat (UserEntity)
        UserEntity candidat = userRepository.findById(candidatId)
                .orElseThrow(() -> new RuntimeException("Candidat non trouvé"));

        // 2. Récupérer l'employé référent (EmployeEntity)
        // Note: On suppose l'existence d'un employeRepository injecté
        EmployeEntity employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new RuntimeException("Employé référent non trouvé"));

        // 3. Appliquer la logique métier définie dans UserServiceImpl
        userService.finaliserEmbauche(candidat, employe); //

        // 4. Retourner le DTO mis à jour
        return ResponseEntity.ok(convertToDTO(candidat));
    }

    // Méthode de mapping (À déplacer idéalement dans un Service ou Mapper dédié)
    private UserDTO convertToDTO(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setNom(entity.getNom());
        dto.setPrenom(entity.getPrenom());
        dto.setEmail(entity.getEmail());
        dto.setTelephone(entity.getTelephone());
        dto.setUserType(entity.getUserType());
        dto.setRoles(entity.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()));
        if (entity.getEmployeProfile() != null) {
            dto.setEmployeProfileId(entity.getEmployeProfile().getId());
        }
        return dto;
    }
}