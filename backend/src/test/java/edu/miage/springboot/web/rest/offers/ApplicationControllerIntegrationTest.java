package edu.miage.springboot.web.rest.offers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.miage.springboot.dao.entities.offers.*;
import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.offers.*;
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
 * Tests d'intégration pour ApplicationController
 * Teste les endpoints de candidature avec authentification
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    private ApplicationEntity testApplication;
    private JobOfferEntity testJobOffer;
    private CandidatEntity testCandidat;
    private EmployeEntity testEmploye;
    private UserEntity candidatUser;
    private UserEntity employeUser;

    @BeforeEach
    void setUp() {
        // Nettoyage
        applicationRepository.deleteAll();
        jobOfferRepository.deleteAll();
        candidatRepository.deleteAll();
        employeRepository.deleteAll();
        userRepository.deleteAll();

        // Création des rôles
        UserRoleEntity candidatRole = userRoleRepository.findByName("ROLE_CANDIDAT")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_CANDIDAT");
                    return userRoleRepository.save(role);
                });

        UserRoleEntity employeRole = userRoleRepository.findByName("ROLE_EMPLOYE")
                .orElseGet(() -> {
                    UserRoleEntity role = new UserRoleEntity();
                    role.setName("ROLE_EMPLOYE");
                    return userRoleRepository.save(role);
                });

        // Création d'un utilisateur candidat
        candidatUser = new UserEntity();
        candidatUser.setUsername("candidat@test.com");
        candidatUser.setPassword("password");
        candidatUser.setUserType(UserTypeEnum.CANDIDAT);
        Set<UserRoleEntity> candidatRoles = new HashSet<>();
        candidatRoles.add(candidatRole);
        candidatUser.setRoles(candidatRoles);
        candidatUser = userRepository.save(candidatUser);

        // Création d'un profil candidat
        testCandidat = new CandidatEntity();
        testCandidat.setUser(candidatUser);
        testCandidat.setConsentDate(LocalDateTime.now().minusMonths(6));
        testCandidat.setArchived(false);
        testCandidat = candidatRepository.save(testCandidat);

        // Création d'un utilisateur employé
        employeUser = new UserEntity();
        employeUser.setUsername("employe@test.com");
        employeUser.setPassword("password");
        employeUser.setUserType(UserTypeEnum.EMPLOYE);
        Set<UserRoleEntity> employeRoles = new HashSet<>();
        employeRoles.add(employeRole);
        employeUser.setRoles(employeRoles);
        employeUser = userRepository.save(employeUser);

        // Création d'un profil employé
        testEmploye = new EmployeEntity();
        testEmploye.setUser(employeUser);
        testEmploye.setPoste("Manager");
        testEmploye.setDepartement("IT");
        testEmploye = employeRepository.save(testEmploye);

        // Création d'une offre d'emploi
        testJobOffer = new JobOfferEntity();
        testJobOffer.setTitle("Développeur Full Stack");
        testJobOffer.setDescription("Poste de développeur full stack");
        testJobOffer.setLocation("Paris");
        testJobOffer.setDepartment("IT");
        testJobOffer.setStatus(JobStatusEnum.OPEN);
        testJobOffer.setCreator(testEmploye);
        testJobOffer.setSalaryRange(50000.0);
        testJobOffer.setRemoteDays(2);
        testJobOffer = jobOfferRepository.save(testJobOffer);

        // Création d'une candidature
        testApplication = new ApplicationEntity();
        testApplication.setJob(testJobOffer);
        testApplication.setCandidate(testCandidat);
        testApplication.setCvUrl("http://example.com/cv.pdf");
        testApplication.setCoverLetter("Lettre de motivation");
        testApplication.setCurrentStatus(ApplicationStatusEnum.RECEIVED);
        testApplication = applicationRepository.save(testApplication);
    }

    @Test
    @WithMockUser(username = "candidat@test.com", authorities = {"ROLE_CANDIDAT"})
    void testApply_WithValidData_ShouldCreateApplication() throws Exception {
        // Créer une nouvelle offre pour ce test
        JobOfferEntity newOffer = new JobOfferEntity();
        newOffer.setTitle("Nouvelle offre");
        newOffer.setDescription("Description");
        newOffer.setLocation("Lyon");
        newOffer.setDepartment("R&D");
        newOffer.setStatus(JobStatusEnum.OPEN);
        newOffer.setCreator(testEmploye);
        newOffer.setSalaryRange(45000.0);
        newOffer = jobOfferRepository.save(newOffer);

        mockMvc.perform(post("/api/applications/apply")
                        .param("jobOfferId", newOffer.getId().toString())
                        .param("candidateId", testCandidat.getId().toString())
                        .param("cvUrl", "http://example.com/new-cv.pdf")
                        .param("coverLetter", "Ma lettre de motivation"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentStatus").value("RECEIVED"));
    }

    @Test
    void testApply_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/applications/apply")
                        .param("jobOfferId", testJobOffer.getId().toString())
                        .param("candidateId", testCandidat.getId().toString())
                        .param("cvUrl", "http://example.com/cv.pdf"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "candidat@test.com", authorities = {"ROLE_CANDIDAT"})
    void testApply_WhenAlreadyApplied_ShouldReturnBadRequest() throws Exception {
        // On réutilise l'offre déjà candidatée dans le setUp
        mockMvc.perform(post("/api/applications/apply")
                        .param("jobOfferId", testJobOffer.getId().toString())
                        .param("candidateId", testCandidat.getId().toString())
                        .param("cvUrl", "http://example.com/cv.pdf"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "candidat@test.com", authorities = {"ROLE_CANDIDAT"})
    void testApply_WithArchivedCandidate_ShouldReturnForbidden() throws Exception {
        // Archiver le candidat
        testCandidat.setArchived(true);
        candidatRepository.save(testCandidat);

        mockMvc.perform(post("/api/applications/apply")
                        .param("jobOfferId", testJobOffer.getId().toString())
                        .param("candidateId", testCandidat.getId().toString())
                        .param("cvUrl", "http://example.com/cv.pdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testGetAllApplications_WithRHRole_ShouldReturnAllApplications() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(username = "candidat@test.com", authorities = {"ROLE_CANDIDAT"})
    void testGetAllApplications_WithCandidatRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = {"ROLE_ADMIN"})
    void testGetApplicationById_WithAdminRole_ShouldReturnApplication() throws Exception {
        mockMvc.perform(get("/api/applications/{id}", testApplication.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testApplication.getId()))
                .andExpect(jsonPath("$.currentStatus").value("RECEIVED"));
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testGetApplicationById_WithRHRole_ShouldReturnApplication() throws Exception {
        mockMvc.perform(get("/api/applications/{id}", testApplication.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testApplication.getId()));
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testGetApplicationById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/applications/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = {"ROLE_ADMIN"})
    void testGetApplicationsByCandidate_ShouldReturnCandidateApplications() throws Exception {
        mockMvc.perform(get("/api/applications/candidate/{candidateId}", testCandidat.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].candidateId").value(testCandidat.getId()));
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testUpdateStatus_ToInterviewPending_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "INTERVIEW_PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("INTERVIEW_PENDING"));
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testUpdateStatus_ToRejected_WithReason_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "REJECTED")
                        .param("reason", "Profil ne correspondant pas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("REJECTED"));
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testUpdateStatus_ToRejected_WithoutReason_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "REJECTED"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "candidat@test.com", authorities = {"ROLE_CANDIDAT"})
    void testUpdateStatus_WithCandidatRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "INTERVIEW_PENDING"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testUpdateStatus_ToHired_ShouldConvertCandidateToEmployee() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "HIRED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("HIRED"));

        // Vérifier que l'offre est passée à FILLED
        JobOfferEntity updatedOffer = jobOfferRepository.findById(testJobOffer.getId()).orElseThrow();
        assert updatedOffer.getStatus() == JobStatusEnum.FILLED;

        // Vérifier que le candidat est archivé
        CandidatEntity updatedCandidat = candidatRepository.findById(testCandidat.getId()).orElseThrow();
        assert updatedCandidat.isArchived();

        // Vérifier que l'utilisateur est devenu EMPLOYE
        UserEntity updatedUser = userRepository.findById(candidatUser.getId()).orElseThrow();
        assert updatedUser.getUserType() == UserTypeEnum.EMPLOYE;
    }

    @Test
    @WithMockUser(username = "admin@test.com", authorities = {"ROLE_ADMIN"})
    void testUpdateStatus_WithAdminRole_ShouldWork() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "TECHNICAL_TEST_PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("TECHNICAL_TEST_PENDING"));
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testUpdateStatus_ToOfferPending_ShouldUpdateStatus() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", testApplication.getId())
                        .param("status", "OFFER_PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("OFFER_PENDING"));
    }

    @Test
    @WithMockUser(username = "rh@test.com", authorities = {"ROLE_RH"})
    void testUpdateStatus_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(patch("/api/applications/{id}/status", 99999L)
                        .param("status", "INTERVIEW_PENDING"))
                .andExpect(status().isBadRequest());
    }
}
