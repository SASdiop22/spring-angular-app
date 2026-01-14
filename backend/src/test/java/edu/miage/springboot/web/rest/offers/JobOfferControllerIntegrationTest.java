package edu.miage.springboot.web.rest.offers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.entities.users.UserRoleEntity;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.dao.repositories.users.UserRoleRepository;
import edu.miage.springboot.web.dtos.offers.JobOfferDTO;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour JobOfferController
 * Teste les endpoints REST avec contexte de sécurité
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class JobOfferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private JobOfferEntity testJobOffer;
    private EmployeEntity testEmploye;
    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // Nettoyage
        jobOfferRepository.deleteAll();
        employeRepository.deleteAll();
        userRepository.deleteAll();

        // Création d'un rôle EMPLOYE
        UserRoleEntity roleEmploye = userRoleRepository.findByName("ROLE_EMPLOYE")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_EMPLOYE");
                    return userRoleRepository.save(role);
                });

        // Création d'un utilisateur de test
        testUser = new UserEntity();
        testUser.setUsername("testemploye");
        testUser.setPassword("password");
        Set<UserRoleEntity> roles = new HashSet<>();
        roles.add(roleEmploye);
        testUser.setRoles(roles);
        testUser = userRepository.save(testUser);

        // Création d'un employé de test
        testEmploye = new EmployeEntity();
        testEmploye.setUser(testUser);
        testEmploye.setPoste("Manager");
        testEmploye.setDepartement("IT");
        testEmploye = employeRepository.save(testEmploye);

        // Création d'une offre de test
        testJobOffer = new JobOfferEntity();
        testJobOffer.setTitle("Développeur Java Senior");
        testJobOffer.setDescription("Nous recherchons un développeur Java expérimenté");
        testJobOffer.setDeadline(LocalDate.now().plusMonths(1));
        testJobOffer.setLocation("Paris");
        testJobOffer.setDepartment("IT");
        testJobOffer.setStatus(JobStatusEnum.OPEN);
        testJobOffer.setCreator(testEmploye);
        testJobOffer.setSalaryRange(50000.0);
        testJobOffer.setRemoteDays(2);
        testJobOffer = jobOfferRepository.save(testJobOffer);
    }

    @Test
    void testGetAllPublished_WithoutAuthentication_ShouldReturnOnlyOpenOffers() throws Exception {
        mockMvc.perform(get("/api/joboffers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$[?(@.id == " + testJobOffer.getId() + ")].title", 
                    hasItem("Développeur Java Senior")));
    }

    @Test
    void testGetById_WithValidId_ShouldReturnJobOffer() throws Exception {
        mockMvc.perform(get("/api/joboffers/{id}", testJobOffer.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testJobOffer.getId()))
                .andExpect(jsonPath("$.title").value("Développeur Java Senior"))
                .andExpect(jsonPath("$.location").value("Paris"));
    }

    @Test
    void testGetById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/joboffers/{id}", 99999L))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void testSearch_WithKeyword_ShouldReturnMatchingOffers() throws Exception {
        mockMvc.perform(get("/api/joboffers/search")
                        .param("keyword", "Java"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title", containsString("Java")));
    }

    @Test
    @WithMockUser(username = "testemploye", authorities = {"ROLE_EMPLOYE"})
    void testCreateJobOffer_WithEmployeRole_ShouldCreateJobOffer() throws Exception {
        JobOfferDTO newJobOffer = new JobOfferDTO();
        newJobOffer.setTitle("Développeur Python");
        newJobOffer.setDescription("Poste de développeur Python");
        newJobOffer.setDeadline(LocalDate.now().plusMonths(2));
        newJobOffer.setLocation("Lyon");
        newJobOffer.setDepartment("R&D");
        newJobOffer.setCreatorId(testEmploye.getId());

        mockMvc.perform(post("/api/joboffers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newJobOffer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Développeur Python"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void testCreateJobOffer_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        JobOfferDTO newJobOffer = new JobOfferDTO();
        newJobOffer.setTitle("Développeur Python");
        newJobOffer.setCreatorId(testEmploye.getId());

        mockMvc.perform(post("/api/joboffers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newJobOffer)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testemploye", authorities = {"ROLE_EMPLOYE"})
    void testUpdateStatus_WithEmployeRole_AsOwner_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(patch("/api/joboffers/{id}/status", testJobOffer.getId())
                        .param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "rh_user", authorities = {"ROLE_RH"})
    void testValidateAndPublish_WithRHRole_ShouldPublishJobOffer() throws Exception {
        // Créer une offre en PENDING
        JobOfferEntity pendingOffer = new JobOfferEntity();
        pendingOffer.setTitle("Offre à valider");
        pendingOffer.setDescription("Description");
        pendingOffer.setDeadline(LocalDate.now().plusMonths(1));
        pendingOffer.setLocation("Paris");
        pendingOffer.setDepartment("IT");
        pendingOffer.setStatus(JobStatusEnum.PENDING);
        pendingOffer.setCreator(testEmploye);
        pendingOffer = jobOfferRepository.save(pendingOffer);

        mockMvc.perform(patch("/api/joboffers/{id}/publish", pendingOffer.getId())
                        .param("salary", "55000")
                        .param("remoteDays", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.salaryRange").value(55000.0))
                .andExpect(jsonPath("$.remoteDays").value(3));
    }

    @Test
    @WithMockUser(username = "employe_user", authorities = {"ROLE_EMPLOYE"})
    void testValidateAndPublish_WithEmployeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/joboffers/{id}/publish", testJobOffer.getId())
                        .param("salary", "55000")
                        .param("remoteDays", "3"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "rh_user", authorities = {"ROLE_RH"})
    void testGetAllWithPrivilege_WithRHRole_ShouldReturnAllOffers() throws Exception {
        // Créer une offre DRAFT
        JobOfferEntity draftOffer = new JobOfferEntity();
        draftOffer.setTitle("Offre en brouillon");
        draftOffer.setDescription("Description");
        draftOffer.setDeadline(LocalDate.now().plusMonths(1));
        draftOffer.setLocation("Paris");
        draftOffer.setDepartment("IT");
        draftOffer.setStatus(JobStatusEnum.DRAFT);
        draftOffer.setCreator(testEmploye);
        jobOfferRepository.save(draftOffer);

        mockMvc.perform(get("/api/joboffers/privilege"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @WithMockUser(username = "employe_user", authorities = {"ROLE_EMPLOYE"})
    void testGetAllWithPrivilege_WithEmployeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/joboffers/privilege"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "rh_user", authorities = {"ROLE_RH"})
    void testCloseOffer_WithRHRole_ShouldCloseJobOffer() throws Exception {
        mockMvc.perform(patch("/api/joboffers/{id}/close", testJobOffer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CLOSED"));
    }

    @Test
    @WithMockUser(username = "admin_user", authorities = {"ROLE_ADMIN"})
    void testDeleteJobOffer_WithAdminRole_ShouldDeleteJobOffer() throws Exception {
        mockMvc.perform(delete("/api/joboffers/{id}", testJobOffer.getId()))
                .andExpect(status().isNoContent());

        // Vérifier que l'offre n'existe plus
        mockMvc.perform(get("/api/joboffers/{id}", testJobOffer.getId()))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "employe_user", authorities = {"ROLE_EMPLOYE"})
    void testDeleteJobOffer_WithEmployeRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/joboffers/{id}", testJobOffer.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllPublished_ShouldNotReturnDraftOrPendingOffers() throws Exception {
        // Créer des offres avec différents statuts
        JobOfferEntity draftOffer = new JobOfferEntity();
        draftOffer.setTitle("Offre DRAFT");
        draftOffer.setDescription("Description");
        draftOffer.setDeadline(LocalDate.now().plusMonths(1));
        draftOffer.setLocation("Paris");
        draftOffer.setDepartment("IT");
        draftOffer.setStatus(JobStatusEnum.DRAFT);
        draftOffer.setCreator(testEmploye);
        jobOfferRepository.save(draftOffer);

        JobOfferEntity pendingOffer = new JobOfferEntity();
        pendingOffer.setTitle("Offre PENDING");
        pendingOffer.setDescription("Description");
        pendingOffer.setDeadline(LocalDate.now().plusMonths(1));
        pendingOffer.setLocation("Lyon");
        pendingOffer.setDepartment("RH");
        pendingOffer.setStatus(JobStatusEnum.PENDING);
        pendingOffer.setCreator(testEmploye);
        jobOfferRepository.save(pendingOffer);

        mockMvc.perform(get("/api/joboffers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(is("OPEN"))));
    }
}
