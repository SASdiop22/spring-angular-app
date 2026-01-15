package edu.miage.springboot.web.rest.users;

import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.services.impl.users.UserServiceImpl;
import edu.miage.springboot.services.interfaces.EmployeService;
import edu.miage.springboot.utils.mappers.UserMapper;
import edu.miage.springboot.web.dtos.users.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmployeService employeService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userMapper.toDtos(userRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("@securityService.hasPrivilegedRole()")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(userMapper.toDto(user)))
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
    @PreAuthorize("@securityService.hasPrivilegedRole()")
    public ResponseEntity<UserDTO> assignReferent(@PathVariable Long candidatId, @PathVariable Long employeId) {
        // Appeler le service qui va gérer les vérifications d'existence
        UserDTO updatedUser = employeService.assignRecruitmentReferent(candidatId, employeId);
        return ResponseEntity.ok(updatedUser);
    }


}