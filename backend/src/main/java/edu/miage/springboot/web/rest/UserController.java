package edu.miage.springboot.web.rest;

import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.repositories.UserRepository;
import edu.miage.springboot.web.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
        // Logique de mise à jour simplifiée pour l'exemple
        return userRepository.findById(candidatId).map(candidat -> {
            return ResponseEntity.ok(convertToDTO(userRepository.save(candidat)));
        }).orElse(ResponseEntity.notFound().build());
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