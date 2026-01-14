package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.offers.*;
import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.offers.*;
import edu.miage.springboot.dao.repositories.users.*;
import edu.miage.springboot.utils.mappers.ApplicationMapper;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour ApplicationServiceImpl
 * Couvre les fonctionnalités de candidature, mise à jour de statut et processus d'embauche
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private CandidatRepository candidatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private ApplicationMapper applicationMapper;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private ApplicationEntity applicationEntity;
    private ApplicationDTO applicationDTO;
    private JobOfferEntity jobOfferEntity;
    private CandidatEntity candidatEntity;
    private UserEntity userEntity;
    private EmployeEntity employeEntity;
    private UserRoleEntity candidateRole;
    private UserRoleEntity employeRole;

    @BeforeEach
    void setUp() {
        // Initialisation des rôles
        candidateRole = new UserRoleEntity();
        candidateRole.setName("ROLE_CANDIDATE");

        employeRole = new UserRoleEntity();
        employeRole.setName("ROLE_EMPLOYE");

        // Initialisation de l'utilisateur
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("candidate@test.com");
        userEntity.setUserType(UserTypeEnum.CANDIDAT);

        // Initialisation de l'employé créateur
        employeEntity = new EmployeEntity();
        employeEntity.setId(1L);
        employeEntity.setPoste("Manager");
        employeEntity.setDepartement("IT");

        // Initialisation du candidat
        candidatEntity = new CandidatEntity();
        candidatEntity.setId(1L);
        candidatEntity.setUser(userEntity);
        candidatEntity.setConsentDate(LocalDateTime.now().minusMonths(6));
        candidatEntity.setArchived(false);

        // Initialisation de l'offre d'emploi
        jobOfferEntity = new JobOfferEntity();
        jobOfferEntity.setId(1L);
        jobOfferEntity.setTitle("Développeur Java");
        jobOfferEntity.setDepartment("IT");
        jobOfferEntity.setStatus(JobStatusEnum.OPEN);
        jobOfferEntity.setCreator(employeEntity);

        // Initialisation de la candidature
        applicationEntity = new ApplicationEntity();
        applicationEntity.setId(1L);
        applicationEntity.setJob(jobOfferEntity);
        applicationEntity.setCandidate(candidatEntity);
        applicationEntity.setCvUrl("http://example.com/cv.pdf");
        applicationEntity.setCoverLetter("Lettre de motivation");
        applicationEntity.setCurrentStatus(ApplicationStatusEnum.RECEIVED);

        // Initialisation du DTO
        applicationDTO = new ApplicationDTO();
        applicationDTO.setId(1L);
        applicationDTO.setJobId(1L);
        applicationDTO.setCandidateId(1L);
        applicationDTO.setCurrentStatus(ApplicationStatusEnum.RECEIVED);
    }

    @Test
    void testApply_WithValidData_ShouldCreateApplication() {
        // Given
        Long jobOfferId = 1L;
        Long candidateId = 1L;
        String cvUrl = "http://example.com/cv.pdf";
        String coverLetter = "Lettre de motivation";

        when(applicationRepository.existsByJobIdAndCandidateId(jobOfferId, candidateId)).thenReturn(false);
        when(jobOfferRepository.findById(jobOfferId)).thenReturn(Optional.of(jobOfferEntity));
        when(candidatRepository.findById(candidateId)).thenReturn(Optional.of(candidatEntity));
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(applicationEntity);
        when(applicationMapper.toDto(any(ApplicationEntity.class))).thenReturn(applicationDTO);

        // When
        ApplicationDTO result = applicationService.apply(jobOfferId, candidateId, cvUrl, coverLetter);

        // Then
        assertNotNull(result);
        verify(applicationRepository).existsByJobIdAndCandidateId(jobOfferId, candidateId);
        verify(jobOfferRepository).findById(jobOfferId);
        verify(candidatRepository).findById(candidateId);
        verify(applicationRepository).save(any(ApplicationEntity.class));
    }

    @Test
    void testApply_WhenAlreadyApplied_ShouldThrowException() {
        // Given
        when(applicationRepository.existsByJobIdAndCandidateId(1L, 1L)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            applicationService.apply(1L, 1L, "cv.pdf", "cover letter");
        });

        assertEquals("Vous avez déjà postulé à cette offre.", exception.getMessage());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void testApply_WithExpiredConsent_ShouldThrowException() {
        // Given
        candidatEntity.setConsentDate(LocalDateTime.now().minusYears(3));
        
        when(applicationRepository.existsByJobIdAndCandidateId(1L, 1L)).thenReturn(false);
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidatEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.apply(1L, 1L, "cv.pdf", "cover letter");
        });

        assertTrue(exception.getMessage().contains("RGPD"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void testApply_WithNonExistentJobOffer_ShouldThrowException() {
        // Given
        when(applicationRepository.existsByJobIdAndCandidateId(1L, 1L)).thenReturn(false);
        when(jobOfferRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.apply(1L, 1L, "cv.pdf", "cover letter");
        });

        assertEquals("Offre non trouvée", exception.getMessage());
    }

    @Test
    void testApply_WithNonExistentCandidate_ShouldThrowException() {
        // Given
        when(applicationRepository.existsByJobIdAndCandidateId(1L, 1L)).thenReturn(false);
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(candidatRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.apply(1L, 1L, "cv.pdf", "cover letter");
        });

        assertEquals("Candidat non trouvé", exception.getMessage());
    }

    @Test
    void testUpdateStatus_ToInterviewPending_ShouldSetMeetingDate() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(applicationEntity));
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(applicationEntity);
        when(applicationMapper.toDto(any(ApplicationEntity.class))).thenReturn(applicationDTO);

        // When
        ApplicationDTO result = applicationService.updateStatus(1L, ApplicationStatusEnum.INTERVIEW_PENDING, null);

        // Then
        assertNotNull(result);
        assertNotNull(applicationEntity.getMeetingDate());
        verify(applicationRepository).save(applicationEntity);
    }

    @Test
    void testUpdateStatus_ToRejected_WithoutReason_ShouldThrowException() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(applicationEntity));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            applicationService.updateStatus(1L, ApplicationStatusEnum.REJECTED, null);
        });

        assertTrue(exception.getMessage().contains("motif de rejet"));
        verify(applicationRepository, never()).save(any());
    }

    @Test
    void testUpdateStatus_ToRejected_WithReason_ShouldSetRejectionReason() {
        // Given
        String reason = "Profil ne correspondant pas aux attentes";
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(applicationEntity));
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(applicationEntity);
        when(applicationMapper.toDto(any(ApplicationEntity.class))).thenReturn(applicationDTO);

        // When
        ApplicationDTO result = applicationService.updateStatus(1L, ApplicationStatusEnum.REJECTED, reason);

        // Then
        assertNotNull(result);
        assertEquals(reason, applicationEntity.getRejectionReason());
        verify(applicationRepository).save(applicationEntity);
    }

    @Test
    void testUpdateStatus_ToHired_ShouldConvertCandidateToEmployee() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(applicationEntity));
        when(userRoleRepository.findByName("ROLE_EMPLOYE")).thenReturn(Optional.of(employeRole));
        when(employeRepository.save(any(EmployeEntity.class))).thenReturn(employeEntity);
        when(candidatRepository.saveAndFlush(any(CandidatEntity.class))).thenReturn(candidatEntity);
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(userEntity);
        when(jobOfferRepository.save(any(JobOfferEntity.class))).thenReturn(jobOfferEntity);
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(applicationEntity);
        when(applicationMapper.toDto(any(ApplicationEntity.class))).thenReturn(applicationDTO);

        // When
        ApplicationDTO result = applicationService.updateStatus(1L, ApplicationStatusEnum.HIRED, null);

        // Then
        assertNotNull(result);
        assertEquals(JobStatusEnum.FILLED, jobOfferEntity.getStatus());
        assertEquals(UserTypeEnum.EMPLOYE, userEntity.getUserType());
        assertTrue(candidatEntity.isArchived());
        verify(employeRepository).save(any(EmployeEntity.class));
        verify(candidatRepository).saveAndFlush(candidatEntity);
        verify(userRepository).saveAndFlush(userEntity);
        verify(jobOfferRepository).save(jobOfferEntity);
    }

    @Test
    void testUpdateStatus_WithInvalidApplicationId_ShouldThrowException() {
        // Given
        when(applicationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.updateStatus(999L, ApplicationStatusEnum.RECEIVED, null);
        });

        assertEquals("Candidature introuvable", exception.getMessage());
    }

    @Test
    void testFindById_WithValidId_ShouldReturnApplication() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(applicationEntity));
        when(applicationMapper.toDto(applicationEntity)).thenReturn(applicationDTO);

        // When
        ApplicationDTO result = applicationService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(applicationRepository).findById(1L);
    }

    @Test
    void testFindById_WithInvalidId_ShouldThrowException() {
        // Given
        when(applicationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            applicationService.findById(999L);
        });

        assertTrue(exception.getMessage().contains("Candidature introuvable"));
    }

    @Test
    void testFindAll_ShouldReturnAllApplications() {
        // Given
        List<ApplicationEntity> entities = Arrays.asList(applicationEntity);
        List<ApplicationDTO> dtos = Arrays.asList(applicationDTO);
        
        when(applicationRepository.findAll()).thenReturn(entities);
        when(applicationMapper.toDtos(entities)).thenReturn(dtos);

        // When
        List<ApplicationDTO> result = applicationService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findAll();
    }

    @Test
    void testFindByCandidateId_ShouldReturnCandidateApplications() {
        // Given
        Long candidateId = 1L;
        List<ApplicationEntity> entities = Arrays.asList(applicationEntity);
        List<ApplicationDTO> dtos = Arrays.asList(applicationDTO);
        
        when(applicationRepository.findByCandidateId(candidateId)).thenReturn(entities);
        when(applicationMapper.toDtos(entities)).thenReturn(dtos);

        // When
        List<ApplicationDTO> result = applicationService.findByCandidateId(candidateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(applicationRepository).findByCandidateId(candidateId);
    }

    @Test
    void testUpdateStatus_ToTechnicalTestPending_ShouldSetMeetingDate() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(applicationEntity));
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(applicationEntity);
        when(applicationMapper.toDto(any(ApplicationEntity.class))).thenReturn(applicationDTO);

        // When
        ApplicationDTO result = applicationService.updateStatus(1L, ApplicationStatusEnum.TECHNICAL_TEST_PENDING, null);

        // Then
        assertNotNull(result);
        assertNotNull(applicationEntity.getMeetingDate());
        verify(applicationRepository).save(applicationEntity);
    }

    @Test
    void testUpdateStatus_ToOfferPending_ShouldSetMeetingDate() {
        // Given
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(applicationEntity));
        when(applicationRepository.save(any(ApplicationEntity.class))).thenReturn(applicationEntity);
        when(applicationMapper.toDto(any(ApplicationEntity.class))).thenReturn(applicationDTO);

        // When
        ApplicationDTO result = applicationService.updateStatus(1L, ApplicationStatusEnum.OFFER_PENDING, null);

        // Then
        assertNotNull(result);
        assertNotNull(applicationEntity.getMeetingDate());
        verify(applicationRepository).save(applicationEntity);
    }
}
