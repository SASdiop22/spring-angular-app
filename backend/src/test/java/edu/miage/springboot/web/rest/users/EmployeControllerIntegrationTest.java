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
 * Tests d'intégration pour EmployeController
 * Teste les endpoints de gestion des employés
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class EmployeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private EmployeEntity testEmploye;
    private EmployeEntity testEmployeRH;
    private EmployeEntity testEmployeDemandeur;
    private UserEntity testUser;
    private UserEntity testUserRH;
    private UserEntity testUserDemandeur;
    private UserEntity testRecrue;
    private UserRoleEntity roleEmploye;
    private UserRoleEntity roleRH;
    private UserRoleEntity roleAdmin;

    @BeforeEach
    void setUp() {
        // Nettoyage
        employeRepository.deleteAll();
        userRepository.deleteAll();

        // Création des rôles
        roleEmploye = userRoleRepository.findByName("ROLE_EMPLOYE")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_EMPLOYE");
                    return userRoleRepository.save(role);
                });

        roleRH = userRoleRepository.findByName("ROLE_RH")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_RH");
                    return userRoleRepository.save(role);
                });

        roleAdmin = userRoleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_ADMIN");
                    return userRoleRepository.save(role);
                });

        // Création d'un utilisateur employé standard
        testUser = new UserEntity();
        testUser.setUsername("employe_standard");
        testUser.setPassword("password");
        testUser.setNom("Dupont");
        testUser.setPrenom("Jean");
        testUser.setEmail("jean.dupont@test.com");
        testUser.setUserType(UserTypeEnum.EMPLOYE);
        testUser.setCreatedAt(LocalDateTime.now());
        Set<UserRoleEntity> userRoles = new HashSet<>();
        userRoles.add(roleEmploye);
        testUser.setRoles(userRoles);
        testUser = userRepository.save(testUser);

        testEmploye = new EmployeEntity();
        testEmploye.setUser(testUser);
        testEmploye.setPoste("Développeur");
        testEmploye.setDepartement("IT");
        testEmploye.setTelephoneInterne("1234");
        testEmploye.setDemandeurDePoste(false);
        testEmploye.setRhPrivilege(false);
        testEmploye.setAdminPrivilege(false);
        testEmploye = employeRepository.save(testEmploye);

        testUser.setEmployeProfile(testEmploye);
        testUser = userRepository.save(testUser);

        // Création d'un utilisateur RH
        testUserRH = new UserEntity();
        testUserRH.setUsername("employe_rh");
        testUserRH.setPassword("password");
        testUserRH.setNom("Martin");
        testUserRH.setPrenom("Sophie");
        testUserRH.setEmail("sophie.martin@test.com");
        testUserRH.setUserType(UserTypeEnum.EMPLOYE);
        testUserRH.setCreatedAt(LocalDateTime.now());
        Set<UserRoleEntity> rhRoles = new HashSet<>();
        rhRoles.add(roleRH);
        testUserRH.setRoles(rhRoles);
        testUserRH = userRepository.save(testUserRH);

        testEmployeRH = new EmployeEntity();
        testEmployeRH.setUser(testUserRH);
        testEmployeRH.setPoste("Responsable RH");
        testEmployeRH.setDepartement("RH");
        testEmployeRH.setTelephoneInterne("5678");
        testEmployeRH.setDemandeurDePoste(false);
        testEmployeRH.setRhPrivilege(true);
        testEmployeRH.setAdminPrivilege(false);
        testEmployeRH = employeRepository.save(testEmployeRH);

        testUserRH.setEmployeProfile(testEmployeRH);
        testUserRH = userRepository.save(testUserRH);

        // Création d'un employé demandeur de poste
        testUserDemandeur = new UserEntity();
        testUserDemandeur.setUsername("employe_demandeur");
        testUserDemandeur.setPassword("password");
        testUserDemandeur.setNom("Legrand");
        testUserDemandeur.setPrenom("Pierre");
        testUserDemandeur.setEmail("pierre.legrand@test.com");
        testUserDemandeur.setUserType(UserTypeEnum.EMPLOYE);
        testUserDemandeur.setCreatedAt(LocalDateTime.now());
        Set<UserRoleEntity> demandeurRoles = new HashSet<>();
        demandeurRoles.add(roleEmploye);
        testUserDemandeur.setRoles(demandeurRoles);
        testUserDemandeur = userRepository.save(testUserDemandeur);

        testEmployeDemandeur = new EmployeEntity();
        testEmployeDemandeur.setUser(testUserDemandeur);
        testEmployeDemandeur.setPoste("Manager");
        testEmployeDemandeur.setDepartement("R&D");
        testEmployeDemandeur.setTelephoneInterne("9012");
        testEmployeDemandeur.setDemandeurDePoste(true);
        testEmployeDemandeur.setRhPrivilege(false);
        testEmployeDemandeur.setAdminPrivilege(false);
        testEmployeDemandeur = employeRepository.save(testEmployeDemandeur);

        testUserDemandeur.setEmployeProfile(testEmployeDemandeur);
        testUserDemandeur = userRepository.save(testUserDemandeur);

        // Création d'une recrue liée au demandeur
        testRecrue = new UserEntity();
        testRecrue.setUsername("recrue");
        testRecrue.setPassword("password");
        testRecrue.setNom("Nouveau");
        testRecrue.setPrenom("Employé");
        testRecrue.setEmail("nouveau.employe@test.com");
        testRecrue.setUserType(UserTypeEnum.EMPLOYE);
        testRecrue.setCreatedAt(LocalDateTime.now());
        testRecrue.setReferentEmploye(testEmployeDemandeur);
        Set<UserRoleEntity> recrueRoles = new HashSet<>();
        recrueRoles.add(roleEmploye);
        testRecrue.setRoles(recrueRoles);
        testRecrue = userRepository.save(testRecrue);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetRHPersonnel_WithAdminRole_ShouldReturnOnlyRHEmployees() throws Exception {
        mockMvc.perform(get("/api/employes/rh"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.username == 'employe_rh')]").exists())
                .andExpect(jsonPath("$[0].rhPrivilege").value(true));
    }

    @Test
    @WithMockUser(username = "rh", authorities = {"ROLE_RH"})
    void testGetRHPersonnel_WithRHRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/employes/rh"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetRHPersonnel_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/employes/rh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetEmployeById_WithValidId_ShouldReturnEmploye() throws Exception {
        mockMvc.perform(get("/api/employes/{id}", testEmploye.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testEmploye.getId()))
                .andExpect(jsonPath("$.poste").value("Développeur"))
                .andExpect(jsonPath("$.departement").value("IT"))
                .andExpect(jsonPath("$.username").value("employe_standard"))
                .andExpect(jsonPath("$.email").value("jean.dupont@test.com"));
    }

    @Test
    void testGetEmployeById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/employes/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetEmployeById_ShouldWorkWithoutAuthentication() throws Exception {
        // Cet endpoint devrait être accessible sans authentification
        mockMvc.perform(get("/api/employes/{id}", testEmploye.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetDemandeurs_WithAdminRole_ShouldReturnDemandeurs() throws Exception {
        mockMvc.perform(get("/api/employes/demandeurs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.username == 'employe_demandeur')]").exists())
                .andExpect(jsonPath("$[0].demandeurDePoste").value(true));
    }

    @Test
    @WithMockUser(username = "rh", authorities = {"ROLE_RH"})
    void testGetDemandeurs_WithRHRole_ShouldReturnDemandeurs() throws Exception {
        mockMvc.perform(get("/api/employes/demandeurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "employe", authorities = {"ROLE_EMPLOYE"})
    void testGetDemandeurs_WithEmployeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/employes/demandeurs"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetDemandeurs_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/employes/demandeurs"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMyRecruits_WithValidEmployeId_ShouldReturnRecruits() throws Exception {
        mockMvc.perform(get("/api/employes/{id}/recrues", testEmployeDemandeur.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[?(@.nom == 'Nouveau')]").exists());
    }

    @Test
    void testGetMyRecruits_WithEmployeWithoutRecruits_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/employes/{id}/recrues", testEmploye.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetMyRecruits_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/employes/{id}/recrues", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMyRecruits_ShouldWorkWithoutAuthentication() throws Exception {
        // Cet endpoint devrait être accessible sans authentification
        mockMvc.perform(get("/api/employes/{id}/recrues", testEmployeDemandeur.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetRHPersonnel_ShouldReturnCorrectStructure() throws Exception {
        mockMvc.perform(get("/api/employes/rh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].poste").isString())
                .andExpect(jsonPath("$[0].departement").isString())
                .andExpect(jsonPath("$[0].rhPrivilege").isBoolean())
                .andExpect(jsonPath("$[0].demandeurDePoste").isBoolean())
                .andExpect(jsonPath("$[0].adminPrivilege").isBoolean());
    }

    @Test
    void testGetEmployeById_ShouldIncludeAllFields() throws Exception {
        mockMvc.perform(get("/api/employes/{id}", testEmployeRH.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testEmployeRH.getId()))
                .andExpect(jsonPath("$.poste").value("Responsable RH"))
                .andExpect(jsonPath("$.departement").value("RH"))
                .andExpect(jsonPath("$.telephoneInterne").value("5678"))
                .andExpect(jsonPath("$.rhPrivilege").value(true))
                .andExpect(jsonPath("$.demandeurDePoste").value(false))
                .andExpect(jsonPath("$.adminPrivilege").value(false))
                .andExpect(jsonPath("$.nomComplet").value("Sophie Martin"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetDemandeurs_ShouldOnlyReturnDemandeursDePoste() throws Exception {
        mockMvc.perform(get("/api/employes/demandeurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].demandeurDePoste", everyItem(is(true))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetRHPersonnel_ShouldNotIncludeNonRHEmployees() throws Exception {
        mockMvc.perform(get("/api/employes/rh"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].rhPrivilege", everyItem(is(true))));
    }

    @Test
    void testGetMyRecruits_ShouldReturnSimplifiedUserDTO() throws Exception {
        mockMvc.perform(get("/api/employes/{id}/recrues", testEmployeDemandeur.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].nom").isString())
                .andExpect(jsonPath("$[0].prenom").isString())
                // Vérifier que les champs non inclus ne sont pas présents
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void testGetDemandeurs_ShouldIncludeManagerFromRnD() throws Exception {
        mockMvc.perform(get("/api/employes/demandeurs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.poste == 'Manager' && @.departement == 'R&D')]").exists());
    }
}
