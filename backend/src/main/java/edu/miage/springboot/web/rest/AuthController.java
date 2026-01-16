package edu.miage.springboot.web.rest;

import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.entities.users.UserRoleEntity;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.dao.repositories.users.UserRoleRepository;
import edu.miage.springboot.security.JwtService;
import edu.miage.springboot.web.dtos.auth.AuthRequestDTO;
import edu.miage.springboot.web.dtos.auth.AuthResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
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
            // Récupérer l'utilisateur pour obtenir son ID et les rôles
            UserEntity user = userRepository.findByUsername(authRequestDTO.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Générer le token en passant aussi l'ID et les autorités (rôles)
            String token = jwtService.GenerateToken(
                    authRequestDTO.getUsername(),
                    user.getId(),
                    authentication.getAuthorities()
            );
            return new AuthResponseDTO(token);
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

        // ASSIGNATION DU RÔLE PAR DÉFAUT (CANDIDAT)
        UserRoleEntity candidateRole = userRoleRepository.findByName("CANDIDAT")
                .orElseThrow(() -> new RuntimeException("Rôle par défaut introuvable. Avez-vous lancé le Seeder ?"));

        newUser.setRoles(Collections.singleton(candidateRole));

        // SAUVEGARDE
        userRepository.save(newUser);

        return "Utilisateur enregistré avec succès !";
    }
}