package edu.miage.springboot.web.rest;

import edu.miage.springboot.dao.entities.UserEntity;
import edu.miage.springboot.dao.entities.UserRoleEntity;
import edu.miage.springboot.dao.repositories.UserRepository;
import edu.miage.springboot.dao.repositories.UserRoleRepository;
import edu.miage.springboot.security.JwtService;
import edu.miage.springboot.web.dtos.AuthRequestDTO;
import edu.miage.springboot.web.dtos.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponseDTO AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return new AuthResponseDTO(jwtService.GenerateToken(authRequestDTO.getUsername()));
        } else {
            throw new RuntimeException("Utilisateur non trouvé ou mot de passe incorrect");
        }
    }

    @PostMapping("/register")
    public String register(@RequestBody AuthRequestDTO registerRequest) {
        // Vérification si l'utilisateur existe déjà
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Le nom d'utilisateur est déjà pris");
        }

        // Création de l'entité utilisateur
        UserEntity newUser = new UserEntity();
        newUser.setUsername(registerRequest.getUsername());

        // ENCODAGE DU MOT DE PASSE (CRUCIAL)
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Email généré automatiquement (peut être modifié plus tard par l'utilisateur)
        newUser.setEmail(registerRequest.getUsername() + "@candidat.local");

        // ASSIGNATION DU RÔLE PAR DÉFAUT (ROLE_CANDIDATE)
        UserRoleEntity candidateRole = userRoleRepository.findByName("ROLE_CANDIDATE")
                .orElseThrow(() -> new RuntimeException("Rôle par défaut introuvable. Avez-vous lancé le Seeder ?"));

        newUser.setRoles(Collections.singleton(candidateRole));

        // SAUVEGARDE
        userRepository.save(newUser);

        return "Utilisateur enregistré avec succès !";
    }
}