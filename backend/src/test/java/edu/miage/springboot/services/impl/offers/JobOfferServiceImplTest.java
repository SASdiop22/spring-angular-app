package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.utils.mappers.JobOfferMapper;
import edu.miage.springboot.web.dtos.offers.JobOfferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour JobOfferServiceImpl
 * Couvre les fonctionnalités de création, recherche, mise à jour et publication d'offres
 */
@ExtendWith(MockitoExtension.class)
class JobOfferServiceImplTest {

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private JobOfferMapper jobOfferMapper;

    @Mock
    private EmployeRepository employeRepository;

    @InjectMocks
    private JobOfferServiceImpl jobOfferService;

    private JobOfferEntity jobOfferEntity;
    private JobOfferDTO jobOfferDTO;
    private EmployeEntity employeEntity;

    @BeforeEach
    void setUp() {
        // Initialisation d'un employé créateur
        employeEntity = new EmployeEntity();
        employeEntity.setId(1L);
        employeEntity.setPoste("Manager");

        // Initialisation d'une offre d'emploi
        jobOfferEntity = new JobOfferEntity();
        jobOfferEntity.setId(1L);
        jobOfferEntity.setTitle("Développeur Java");
        jobOfferEntity.setDescription("Développeur Java expérimenté");
        jobOfferEntity.setDeadline(LocalDate.now().plusMonths(1));
        jobOfferEntity.setLocation("Paris");
        jobOfferEntity.setDepartment("IT");
        jobOfferEntity.setStatus(JobStatusEnum.DRAFT);
        jobOfferEntity.setCreator(employeEntity);
        jobOfferEntity.setCreatedAt(LocalDateTime.now());

        // Initialisation du DTO
        jobOfferDTO = new JobOfferDTO();
        jobOfferDTO.setId(1L);
        jobOfferDTO.setTitle("Développeur Java");
        jobOfferDTO.setDescription("Développeur Java expérimenté");
        jobOfferDTO.setDeadline(LocalDate.now().plusMonths(1));
        jobOfferDTO.setLocation("Paris");
        jobOfferDTO.setDepartment("IT");
        jobOfferDTO.setStatus(JobStatusEnum.DRAFT);
        jobOfferDTO.setCreatorId(1L);
    }

    @Test
    void testFindAll_ShouldReturnAllJobOffers() {
        // Given
        List<JobOfferEntity> entities = Arrays.asList(jobOfferEntity);
        List<JobOfferDTO> dtos = Arrays.asList(jobOfferDTO);
        
        when(jobOfferRepository.findAll()).thenReturn(entities);
        when(jobOfferMapper.entitiesToDtos(entities)).thenReturn(dtos);

        // When
        List<JobOfferDTO> result = jobOfferService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Développeur Java", result.get(0).getTitle());
        verify(jobOfferRepository, times(1)).findAll();
        verify(jobOfferMapper, times(1)).entitiesToDtos(entities);
    }

    @Test
    void testFindById_WhenJobOfferExists_ShouldReturnJobOffer() {
        // Given
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(jobOfferMapper.entityToDto(jobOfferEntity)).thenReturn(jobOfferDTO);

        // When
        JobOfferDTO result = jobOfferService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals("Développeur Java", result.getTitle());
        assertEquals(1L, result.getId());
        verify(jobOfferRepository, times(1)).findById(1L);
        verify(jobOfferMapper, times(1)).entityToDto(jobOfferEntity);
    }

    @Test
    void testFindById_WhenJobOfferDoesNotExist_ShouldThrowException() {
        // Given
        when(jobOfferRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobOfferService.findById(999L);
        });
        
        assertEquals("Offre introuvable", exception.getMessage());
        verify(jobOfferRepository, times(1)).findById(999L);
    }

    @Test
    void testFindAllOpen_ShouldReturnOnlyOpenJobOffers() {
        // Given
        jobOfferEntity.setStatus(JobStatusEnum.OPEN);
        jobOfferDTO.setStatus(JobStatusEnum.OPEN);
        
        List<JobOfferEntity> entities = Arrays.asList(jobOfferEntity);
        List<JobOfferDTO> dtos = Arrays.asList(jobOfferDTO);
        
        when(jobOfferRepository.findByStatus(JobStatusEnum.OPEN)).thenReturn(entities);
        when(jobOfferMapper.entitiesToDtos(entities)).thenReturn(dtos);

        // When
        List<JobOfferDTO> result = jobOfferService.findAllOpen();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(JobStatusEnum.OPEN, result.get(0).getStatus());
        verify(jobOfferRepository, times(1)).findByStatus(JobStatusEnum.OPEN);
    }

    @Test
    void testCreateJobOffer_ShouldCreateJobOfferWithDraftStatus() {
        // Given
        when(jobOfferMapper.dtoToEntity(jobOfferDTO)).thenReturn(jobOfferEntity);
        when(employeRepository.findById(1L)).thenReturn(Optional.of(employeEntity));
        when(jobOfferRepository.save(any(JobOfferEntity.class))).thenReturn(jobOfferEntity);
        when(jobOfferMapper.entityToDto(jobOfferEntity)).thenReturn(jobOfferDTO);

        // When
        JobOfferDTO result = jobOfferService.createJobOffer(jobOfferDTO);

        // Then
        assertNotNull(result);
        verify(employeRepository, times(1)).findById(1L);
        verify(jobOfferRepository, times(1)).save(any(JobOfferEntity.class));
    }

    @Test
    void testCreateJobOffer_WhenCreatorNotFound_ShouldThrowException() {
        // Given
        when(jobOfferMapper.dtoToEntity(jobOfferDTO)).thenReturn(jobOfferEntity);
        when(employeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jobOfferService.createJobOffer(jobOfferDTO);
        });
        
        assertEquals("Employé créateur introuvable", exception.getMessage());
        verify(employeRepository, times(1)).findById(1L);
        verify(jobOfferRepository, never()).save(any());
    }

    @Test
    void testEnrichAndPublish_ShouldPublishJobOfferWithSalaryAndRemoteDays() {
        // Given
        jobOfferEntity.setStatus(JobStatusEnum.PENDING);
        Double salary = 50000.0;
        Integer remoteDays = 2;
        
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(jobOfferRepository.save(any(JobOfferEntity.class))).thenReturn(jobOfferEntity);
        when(jobOfferMapper.entityToDto(jobOfferEntity)).thenReturn(jobOfferDTO);

        // When
        JobOfferDTO result = jobOfferService.enrichAndPublish(1L, salary, remoteDays);

        // Then
        assertNotNull(result);
        verify(jobOfferRepository, times(1)).findById(1L);
        verify(jobOfferRepository, times(1)).save(jobOfferEntity);
        assertEquals(JobStatusEnum.OPEN, jobOfferEntity.getStatus());
        assertEquals(salary, jobOfferEntity.getSalaryRange());
        assertEquals(remoteDays, jobOfferEntity.getRemoteDays());
        assertNotNull(jobOfferEntity.getPublishedAt());
    }

    @Test
    void testEnrichAndPublish_WhenJobOfferIsClosed_ShouldThrowException() {
        // Given
        jobOfferEntity.setStatus(JobStatusEnum.CLOSED);
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            jobOfferService.enrichAndPublish(1L, 50000.0, 2);
        });
        
        assertTrue(exception.getMessage().contains("Impossible de publier"));
        verify(jobOfferRepository, times(1)).findById(1L);
        verify(jobOfferRepository, never()).save(any());
    }

    @Test
    void testUpdateStatus_ToPending_ShouldSubmitForApproval() {
        // Given
        jobOfferEntity.setStatus(JobStatusEnum.DRAFT);
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(jobOfferRepository.save(any(JobOfferEntity.class))).thenReturn(jobOfferEntity);
        when(jobOfferMapper.entityToDto(jobOfferEntity)).thenReturn(jobOfferDTO);

        // When
        JobOfferDTO result = jobOfferService.updateStatus(1L, JobStatusEnum.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(JobStatusEnum.PENDING, jobOfferEntity.getStatus());
        verify(jobOfferRepository, times(1)).save(jobOfferEntity);
    }

    @Test
    void testUpdateStatus_ToOpen_WithoutSalary_ShouldThrowException() {
        // Given
        jobOfferEntity.setStatus(JobStatusEnum.PENDING);
        jobOfferEntity.setSalaryRange(null);
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            jobOfferService.updateStatus(1L, JobStatusEnum.OPEN);
        });
        
        assertTrue(exception.getMessage().contains("salaire"));
        verify(jobOfferRepository, never()).save(any());
    }

    @Test
    void testUpdateStatus_ToOpen_WithSalary_ShouldPublishJobOffer() {
        // Given
        jobOfferEntity.setStatus(JobStatusEnum.PENDING);
        jobOfferEntity.setSalaryRange(50000.0);
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(jobOfferRepository.save(any(JobOfferEntity.class))).thenReturn(jobOfferEntity);
        when(jobOfferMapper.entityToDto(jobOfferEntity)).thenReturn(jobOfferDTO);

        // When
        JobOfferDTO result = jobOfferService.updateStatus(1L, JobStatusEnum.OPEN);

        // Then
        assertNotNull(result);
        assertEquals(JobStatusEnum.OPEN, jobOfferEntity.getStatus());
        assertNotNull(jobOfferEntity.getPublishedAt());
        verify(jobOfferRepository, times(1)).save(jobOfferEntity);
    }

    @Test
    void testUpdateJobOffer_ShouldUpdateJobOfferFields() {
        // Given
        JobOfferDTO updateDTO = new JobOfferDTO();
        updateDTO.setTitle("Développeur Python");
        updateDTO.setDescription("Description mise à jour");
        updateDTO.setDeadline(LocalDate.now().plusMonths(2));
        updateDTO.setDepartment("R&D");
        updateDTO.setLocation("Lyon");
        updateDTO.setSkillsRequired(Arrays.asList("Python", "Django"));
        
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(jobOfferRepository.save(any(JobOfferEntity.class))).thenReturn(jobOfferEntity);
        when(jobOfferMapper.entityToDto(jobOfferEntity)).thenReturn(updateDTO);

        // When
        JobOfferDTO result = jobOfferService.updateJobOffer(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(jobOfferRepository, times(1)).findById(1L);
        verify(jobOfferRepository, times(1)).save(jobOfferEntity);
    }

    @Test
    void testDeleteJobOffer_ShouldDeleteJobOffer() {
        // Given
        doNothing().when(jobOfferRepository).deleteById(1L);

        // When
        jobOfferService.deleteJobOffer(1L);

        // Then
        verify(jobOfferRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSearchJobOffers_ShouldReturnMatchingJobOffers() {
        // Given
        String keyword = "Java";
        List<JobOfferEntity> entities = Arrays.asList(jobOfferEntity);
        List<JobOfferDTO> dtos = Arrays.asList(jobOfferDTO);
        
        when(jobOfferRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword))
            .thenReturn(entities);
        when(jobOfferMapper.entitiesToDtos(entities)).thenReturn(dtos);

        // When
        List<JobOfferDTO> result = jobOfferService.searchJobOffers(keyword);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(jobOfferRepository, times(1))
            .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    @Test
    void testPublishOffer_WithValidParameters_ShouldPublishSuccessfully() {
        // Given
        jobOfferEntity.setStatus(JobStatusEnum.DRAFT);
        Double salary = 45000.0;
        Integer remoteDays = 3;
        
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));
        when(jobOfferRepository.save(any(JobOfferEntity.class))).thenReturn(jobOfferEntity);
        when(jobOfferMapper.entityToDto(jobOfferEntity)).thenReturn(jobOfferDTO);

        // When
        JobOfferDTO result = jobOfferService.publishOffer(1L, salary, remoteDays);

        // Then
        assertNotNull(result);
        verify(jobOfferRepository, times(1)).save(jobOfferEntity);
        assertEquals(JobStatusEnum.OPEN, jobOfferEntity.getStatus());
    }

    @Test
    void testPublishOffer_WithNullSalary_ShouldThrowException() {
        // Given
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jobOfferService.publishOffer(1L, null, 2);
        });
        
        assertTrue(exception.getMessage().contains("obligatoires"));
        verify(jobOfferRepository, never()).save(any());
    }

    @Test
    void testPublishOffer_WithNullRemoteDays_ShouldThrowException() {
        // Given
        when(jobOfferRepository.findById(1L)).thenReturn(Optional.of(jobOfferEntity));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            jobOfferService.publishOffer(1L, 50000.0, null);
        });
        
        assertTrue(exception.getMessage().contains("obligatoires"));
        verify(jobOfferRepository, never()).save(any());
    }
}
