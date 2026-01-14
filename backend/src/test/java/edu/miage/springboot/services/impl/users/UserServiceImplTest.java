package edu.miage.springboot.services.impl.users;

import edu.miage.springboot.dao.entities.users.*;
import edu.miage.springboot.dao.repositories.users.*;
import edu.miage.springboot.utils.mappers.UserMapper;
import edu.miage.springboot.web.dtos.users.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserServiceImpl
 * Couvre les fonctionnalités de gestion des utilisateurs, rôles et privilèges
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private EmployeRepository employeRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity userEntity;
    private EmployeEntity employeEntity;
    private UserRoleEntity roleEmploye;
    private UserRoleEntity roleRH;
    private UserRoleEntity roleAdmin;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Initialisation des rôles
        roleEmploye = new UserRoleEntity();
        roleEmploye.setName("ROLE_EMPLOYE");

        roleRH = new UserRoleEntity();
        roleRH.setName("ROLE_RH");

        roleAdmin = new UserRoleEntity();
        roleAdmin.setName("ROLE_ADMIN");

        // Initialisation d'un utilisateur
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");
        userEntity.setPassword("password");
        userEntity.setUserType(UserTypeEnum.EMPLOYE);
        userEntity.setCreatedAt(LocalDateTime.now().minusMonths(6));
        Set<UserRoleEntity> roles = new HashSet<>();
        roles.add(roleEmploye);
        userEntity.setRoles(roles);

        // Initialisation d'un employé
        employeEntity = new EmployeEntity();
        employeEntity.setId(1L);
        employeEntity.setUser(userEntity);
        employeEntity.setPoste("Développeur");
        employeEntity.setDepartement("IT");
        employeEntity.setDemandeurDePoste(false);
        employeEntity.setRhPrivilege(false);
        employeEntity.setAdminPrivilege(false);

        userEntity.setEmployeProfile(employeEntity);

        // Initialisation du DTO
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setUserType(UserTypeEnum.EMPLOYE);
    }

    @Test
    void testCheckUserExists_WhenUserExists_ShouldNotThrowException() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userEntity));

        // When & Then
        assertDoesNotThrow(() -> userService.checkUserExists("testuser"));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testCheckUserExists_WhenUserDoesNotExist_ShouldPrintMessage() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        assertDoesNotThrow(() -> userService.checkUserExists("nonexistent"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testIsConsentValid_WithValidConsent_ShouldReturnTrue() {
        // Given
        userEntity.setCreatedAt(LocalDateTime.now().minusMonths(12));

        // When
        boolean result = userService.isConsentValid(userEntity);

        // Then
        assertTrue(result);
    }

    @Test
    void testIsConsentValid_WithExpiredConsent_ShouldReturnFalse() {
        // Given
        userEntity.setCreatedAt(LocalDateTime.now().minusYears(3));

        // When
        boolean result = userService.isConsentValid(userEntity);

        // Then
        assertFalse(result);
    }

    @Test
    void testFinaliserEmbauche_WithValidData_ShouldSetReferent() {
        // Given
        UserEntity candidat = new UserEntity();
        candidat.setId(2L);
        candidat.setUsername("candidat");

        EmployeEntity referent = new EmployeEntity();
        referent.setId(1L);

        when(userRepository.save(any(UserEntity.class))).thenReturn(candidat);

        // When
        userService.finaliserEmbauche(candidat, referent);

        // Then
        assertEquals(referent, candidat.getReferentEmploye());
        verify(userRepository, times(1)).save(candidat);
    }

    @Test
    void testFinaliserEmbauche_WithNullCandidat_ShouldThrowException() {
        // Given
        EmployeEntity referent = new EmployeEntity();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.finaliserEmbauche(null, referent);
        });

        assertTrue(exception.getMessage().contains("candidat"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testFinaliserEmbauche_WithNullReferent_ShouldThrowException() {
        // Given
        UserEntity candidat = new UserEntity();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.finaliserEmbauche(candidat, null);
        });

        assertTrue(exception.getMessage().contains("référent"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetDemandeurDePosteList_ShouldReturnUsersWithDemandeurDePosteStatus() {
        // Given
        employeEntity.setDemandeurDePoste(true);
        List<UserEntity> allUsers = Arrays.asList(userEntity);
        when(userRepository.findAll()).thenReturn(allUsers);

        // When
        List<UserEntity> result = userService.getDemandeurDePosteList();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getEmployeProfile().isDemandeurDePoste());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testSetDemandeurDePosteStatus_ToTrue_ShouldGrantRole() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByName("ROLE_EMPLOYE")).thenReturn(Optional.of(roleEmploye));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        userService.setDemandeurDePosteStatus(1L, true);

        // Then
        assertTrue(employeEntity.isDemandeurDePoste());
        verify(userRepository, times(1)).save(userEntity);
        verify(userRoleRepository, times(1)).findByName("ROLE_EMPLOYE");
    }

    @Test
    void testSetDemandeurDePosteStatus_ToFalse_ShouldRevokeRole() {
        // Given
        employeEntity.setDemandeurDePoste(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByName("ROLE_EMPLOYE")).thenReturn(Optional.of(roleEmploye));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        userService.setDemandeurDePosteStatus(1L, false);

        // Then
        assertFalse(employeEntity.isDemandeurDePoste());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testSetDemandeurDePosteStatus_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.setDemandeurDePosteStatus(999L, true);
        });

        assertEquals("Utilisateur non trouvé", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSetDemandeurDePosteStatus_WithoutEmployeProfile_ShouldThrowException() {
        // Given
        userEntity.setEmployeProfile(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.setDemandeurDePosteStatus(1L, true);
        });

        assertTrue(exception.getMessage().contains("profil employé"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSetAdminStatus_ToTrue_ShouldGrantAdminRole() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        userService.setAdminStatus(1L, true);

        // Then
        assertTrue(employeEntity.isAdminPrivilege());
        verify(userRepository, times(1)).save(userEntity);
        verify(userRoleRepository, times(1)).findByName("ROLE_ADMIN");
    }

    @Test
    void testSetAdminStatus_ToFalse_ShouldRevokeAdminRole() {
        // Given
        employeEntity.setAdminPrivilege(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        userService.setAdminStatus(1L, false);

        // Then
        assertFalse(employeEntity.isAdminPrivilege());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testSetRhStatus_ToTrue_ShouldGrantRHRole() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByName("ROLE_RH")).thenReturn(Optional.of(roleRH));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        userService.setRhStatus(1L, true);

        // Then
        assertTrue(employeEntity.isRhPrivilege());
        verify(userRepository, times(1)).save(userEntity);
        verify(userRoleRepository, times(1)).findByName("ROLE_RH");
    }

    @Test
    void testSetRhStatus_ToFalse_ShouldRevokeRHRole() {
        // Given
        employeEntity.setRhPrivilege(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRoleRepository.findByName("ROLE_RH")).thenReturn(Optional.of(roleRH));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        // When
        userService.setRhStatus(1L, false);

        // Then
        assertFalse(employeEntity.isRhPrivilege());
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testSetRhStatus_WithoutEmployeProfile_ShouldThrowException() {
        // Given
        userEntity.setEmployeProfile(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.setRhStatus(1L, true);
        });

        assertTrue(exception.getMessage().contains("profil employé"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAssignReferent_WithValidData_ShouldAssignReferent() {
        // Given
        UserEntity candidat = new UserEntity();
        candidat.setId(2L);
        candidat.setUsername("candidat");

        EmployeEntity referent = new EmployeEntity();
        referent.setId(1L);
        referent.setUser(userEntity);

        when(userRepository.findById(2L)).thenReturn(Optional.of(candidat));
        when(employeRepository.findByUserId(1L)).thenReturn(Optional.of(referent));
        when(userRepository.save(any(UserEntity.class))).thenReturn(candidat);
        when(userMapper.toDto(candidat)).thenReturn(userDTO);

        // When
        UserDTO result = userService.assignReferent(2L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(referent, candidat.getReferentEmploye());
        verify(userRepository, times(1)).save(candidat);
    }

    @Test
    void testAssignReferent_WithNonExistentCandidat_ShouldThrowException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            userService.assignReferent(999L, 1L);
        });

        assertTrue(exception.getMessage().contains("Candidat non trouvé"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAssignReferent_WithNonExistentReferent_ShouldThrowException() {
        // Given
        UserEntity candidat = new UserEntity();
        candidat.setId(2L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(candidat));
        when(employeRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            userService.assignReferent(2L, 999L);
        });

        assertTrue(exception.getMessage().contains("Profil Employé référent non trouvé"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testSetAdminStatus_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.setAdminStatus(999L, true);
        });

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }

    @Test
    void testSetRhStatus_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.setRhStatus(999L, true);
        });

        assertEquals("Utilisateur non trouvé", exception.getMessage());
    }
}
