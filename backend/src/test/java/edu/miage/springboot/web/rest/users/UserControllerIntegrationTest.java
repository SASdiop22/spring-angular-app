package edu.miage.springboot.web.rest.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.users.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour UserController
 * Teste les endpoints de gestion des utilisateurs
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private UserEntity testUser;
    private UserEntity testCandidat;
    private EmployeEntity testEmploye;
    private UserRoleEntity roleAdmin;
    private UserRoleEntity roleRH;
    private UserRoleEntity roleEmploye;
    private UserRoleEntity roleCandidat;

    @BeforeEach
    void setUp() {
        // Nettoyage
        userRepository.deleteAll();
        employeRepository.deleteAll();

        // Création des rôles
        roleAdmin = userRoleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_ADMIN");
                    return userRoleRepository.save(role);
                });

        roleRH = userRoleRepository.findByName("ROLE_RH")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_RH");
                    return userRoleRepository.save(role);
                });

        roleEmploye = userRoleRepository.findByName("ROLE_EMPLOYE")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_EMPLOYE");
                    return userRoleRepository.save(role);
                });

        roleCandidat = userRoleRepository.findByName("ROLE_CANDIDAT")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_CANDIDAT");
                    return userRoleRepository.save(role);
                });

        // Création d'un utilisateur employé
        testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setNom("Dupont");
        testUser.setPrenom("Jean");
        testUser.setEmail("jean.dupont@test.com");
        testUser.setTelephone("0123456789");
        testUser.setUserType(UserTypeEnum.EMPLOYE);
        testUser.setCreatedAt(LocalDateTime.now().minusMonths(6));
        Set<UserRoleEntity> userRoles = new HashSet<>();
        userRoles.add(roleEmploye);
        testUser.setRoles(userRoles);
        testUser = userRepository.save(testUser);

        // Création du profil employé
        testEmploye = new EmployeEntity();
        testEmploye.setUser(testUser);
        testEmploye.setPoste("Manager");
        testEmploye.setDepartement("IT");
        testEmploye = employeRepository.save(testEmploye);

        testUser.setEmployeProfile(testEmploye);
        testUser = userRepository.save(testUser);

        // Création d'un candidat
        testCandidat = new UserEntity();
        testCandidat.setUsername("candidat");
        testCandidat.setPassword("password");
        testCandidat.setNom("Martin");
        testCandidat.setPrenom("Sophie");
        testCandidat.setEmail("sophie.martin@test.com");
        testCandidat.setUserType(UserTypeEnum.CANDIDAT);
        testCandidat.setCreatedAt(LocalDateTime.now().minusMonths(3));
        Set<UserRoleEntity> candidatRoles = new HashSet<>();
        candidatRoles.add(roleCandidat);
        testCandidat.setRoles(candidatRoles);
        testCandidat = userRepository.save(testCandidat);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetAllUsers_WithAdminRole_ShouldReturnAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[?(@.username == 'testuser')].nom", hasItem("Dupont")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_EMPLOYE"})
    void testGetAllUsers_WithEmployeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllUsers_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetUserById_WithAdminRole_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.prenom").value("Jean"))
                .andExpect(jsonPath("$.email").value("jean.dupont@test.com"));
    }

    @Test
    @WithMockUser(username = "rh", authorities = {"ROLE_RH"})
    void testGetUserById_WithRHRole_ShouldReturnUser() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "employe", authorities = {"ROLE_EMPLOYE"})
    void testGetUserById_WithEmployeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetUserById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testIsRgpdValid_WithValidConsent_ShouldReturnTrue() throws Exception {
        mockMvc.perform(get("/api/users/{id}/rgpd-check", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testIsRgpdValid_WithExpiredConsent_ShouldReturnFalse() throws Exception {
        // Créer un utilisateur avec consentement expiré
        UserEntity expiredUser = new UserEntity();
        expiredUser.setUsername("expired");
        expiredUser.setPassword("password");
        expiredUser.setCreatedAt(LocalDateTime.now().minusYears(3));
        expiredUser.setUserType(UserTypeEnum.CANDIDAT);
        Set<UserRoleEntity> roles = new HashSet<>();
        roles.add(roleCandidat);
        expiredUser.setRoles(roles);
        expiredUser = userRepository.save(expiredUser);

        mockMvc.perform(get("/api/users/{id}/rgpd-check", expiredUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void testIsRgpdValid_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}/rgpd-check", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "rh", authorities = {"ROLE_RH"})
    void testAssignReferent_WithRHRole_ShouldAssignReferent() throws Exception {
        mockMvc.perform(patch("/api/users/{candidatId}/assign-referent/{employeId}",
                        testCandidat.getId(), testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testCandidat.getId()));

        // Vérifier que le référent a été assigné
        UserEntity updatedCandidat = userRepository.findById(testCandidat.getId()).orElseThrow();
        assert updatedCandidat.getReferentEmploye() != null;
        assert updatedCandidat.getReferentEmploye().getId().equals(testEmploye.getId());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testAssignReferent_WithAdminRole_ShouldAssignReferent() throws Exception {
        mockMvc.perform(patch("/api/users/{candidatId}/assign-referent/{employeId}",
                        testCandidat.getId(), testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCandidat.getId()));
    }

    @Test
    @WithMockUser(username = "employe", authorities = {"ROLE_EMPLOYE"})
    void testAssignReferent_WithEmployeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/users/{candidatId}/assign-referent/{employeId}",
                        testCandidat.getId(), testUser.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAssignReferent_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(patch("/api/users/{candidatId}/assign-referent/{employeId}",
                        testCandidat.getId(), testUser.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "rh", authorities = {"ROLE_RH"})
    void testAssignReferent_WithInvalidCandidatId_ShouldReturnError() throws Exception {
        mockMvc.perform(patch("/api/users/{candidatId}/assign-referent/{employeId}",
                        99999L, testUser.getId()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "rh", authorities = {"ROLE_RH"})
    void testAssignReferent_WithInvalidEmployeId_ShouldReturnError() throws Exception {
        mockMvc.perform(patch("/api/users/{candidatId}/assign-referent/{employeId}",
                        testCandidat.getId(), 99999L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetAllUsers_ShouldReturnUsersWithCorrectStructure() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].username").isString())
                .andExpect(jsonPath("$[0].userType").isString())
                .andExpect(jsonPath("$[0].roles").isArray());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetUserById_ShouldIncludeEmployeProfile() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeProfileId").value(testEmploye.getId()));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetUserById_ForCandidat_ShouldNotHaveEmployeProfile() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testCandidat.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeProfileId").doesNotExist());
    }

    @Test
    void testIsRgpdValid_ShouldWorkWithoutAuthentication() throws Exception {
        // Ce endpoint devrait être accessible sans authentification pour vérifier RGPD
        mockMvc.perform(get("/api/users/{id}/rgpd-check", testUser.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetAllUsers_ShouldIncludeDifferentUserTypes() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.userType == 'EMPLOYE')]").exists())
                .andExpect(jsonPath("$[?(@.userType == 'CANDIDAT')]").exists());
    }
}
