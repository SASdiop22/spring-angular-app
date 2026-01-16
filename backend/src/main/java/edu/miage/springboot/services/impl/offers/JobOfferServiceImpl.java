package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.entities.users.UserTypeEnum;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.utils.mappers.JobOfferMapper;
import edu.miage.springboot.web.dtos.offers.JobOfferDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobOfferServiceImpl implements JobOfferService {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private JobOfferMapper jobOfferMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<JobOfferDTO> findAll() {
        return jobOfferMapper.entitiesToDtos(jobOfferRepository.findAll());
    }

    @Override
    public JobOfferDTO findById(Long id) {
        return jobOfferRepository.findById(id)
                .map(jobOfferMapper::entityToDto)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));
    }

    // --- Spec 2.A & 2.B : Visibilité publique ---
    @Override
    public List<JobOfferDTO> findAllOpen() {
        // On récupère les offres OPEN (actives) et CLOSED (historique visible)
        List<JobOfferEntity> entities = jobOfferRepository.findByStatusIn(
                Arrays.asList(JobStatusEnum.OPEN, JobStatusEnum.CLOSED)
        );
        return jobOfferMapper.entitiesToDtos(entities);
    }

    // --- Spec 2.A : Enrichissement et Publication par RH ---
    @Override
    @Transactional
    public JobOfferDTO enrichAndPublish(Long id, Double salary, Integer remoteDays) {
        JobOfferEntity entity = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // Sécurité métier : On ne publie que ce qui est en attente (PENDING) ou en brouillon (DRAFT)
        if (entity.getStatus() != JobStatusEnum.PENDING && entity.getStatus() != JobStatusEnum.DRAFT) {
            throw new IllegalStateException("Impossible de publier une offre clôturée ou pourvue.");
        }

        // Utilisation de la méthode métier définie dans JobOfferEntity
        entity.validateAndPublish(salary, remoteDays);

        // On peut aussi enregistrer la date de publication
        entity.setPublishedAt(LocalDateTime.now());

        return jobOfferMapper.entityToDto(jobOfferRepository.save(entity));
    }

    @Override
    @Transactional
    public JobOfferDTO updateStatus(Long id, JobStatusEnum status) {
        // 1. Récupération de l'offre
        JobOfferEntity jobOffer = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre d'emploi introuvable avec l'ID : " + id));

        // 2. Application de la logique métier selon le statut visé
        switch (status) {
            case PENDING:
                jobOffer.submitForApproval();
                break;

            case OPEN:
                // Si le passage en OPEN nécessite des données RH (salaire/remote),
                // on vérifie qu'elles existent ou on utilise des valeurs par défaut
                if (jobOffer.getSalary() == null) {
                    throw new IllegalStateException("Impossible de publier : le salaire doit être renseigné par les RH.");
                }
                jobOffer.setStatus(JobStatusEnum.OPEN);
                jobOffer.setPublishedAt(LocalDateTime.now());
                break;

            case CLOSED:
            case FILLED:
                jobOffer.setStatus(status);
                break;

            default:
                jobOffer.setStatus(status);
                break;
        }

        // 3. Sauvegarde et retour du DTO mis à jour
        JobOfferEntity updatedEntity = jobOfferRepository.save(jobOffer);
        return jobOfferMapper.entityToDto(updatedEntity);
    }

    @Override
    @Transactional
    public JobOfferDTO createJobOffer(JobOfferDTO jobOfferDTO) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // 2. Vérifier que c'est un Employé
        if (currentUser.getEmployeProfile() == null) {
            throw new RuntimeException("Accès refusé : Seul un employé peut créer une demande de poste.");
        }

        JobOfferEntity entity = jobOfferMapper.dtoToEntity(jobOfferDTO);

        // 3. Associer le VRAI créateur (pas celui du JSON)
        entity.setCreator(currentUser.getEmployeProfile());

        // 4. Logique Métier : RH vs Demandeur
        boolean isRH = currentUser.getUserType() == UserTypeEnum.RH;

        if (isRH) {
            // Le RH passe directement en PENDING (ou garde ce qu'il a mis si pertinent)
            // On évite le DRAFT inutile pour eux
            entity.setStatus(JobStatusEnum.PENDING);
        } else {
            // Le Demandeur est FORCÉ en DRAFT (Brouillon)
            entity.setStatus(JobStatusEnum.DRAFT);
        }

        return jobOfferMapper.entityToDto(jobOfferRepository.save(entity));
    }

    /**
     * Conforme à la Spec 2.A : Validation et Publication par les RH
     */
    @Transactional
    public JobOfferDTO publishOffer(Long id, Double salary, Integer remoteDays) {
        JobOfferEntity entity = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // Vérifiez que les paramètres ne sont pas nulls avant l'appel métier
        if (salary == null || salary <= 0 || remoteDays == null || remoteDays < 0 || remoteDays > 5) {
            throw new IllegalArgumentException("Données RH invalides (Salaire/Télétravail)");
        }

        // On s'assure que l'offre est prête à être publiée
        if (entity.getStatus() != JobStatusEnum.PENDING && entity.getStatus() != JobStatusEnum.DRAFT) {
            throw new IllegalStateException("L'offre ne peut pas être publiée dans son état actuel : " + entity.getStatus());
        }

        entity.validateAndPublish(salary, remoteDays);
        entity.setPublishedAt(LocalDateTime.now()); // On trace la date de publication réelle

        return jobOfferMapper.entityToDto(jobOfferRepository.save(entity));
    }

    @Override
    @Transactional
    public JobOfferDTO updateJobOffer(Long id, JobOfferDTO dto) {
        JobOfferEntity existing = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setCompanyName(dto.getCompanyName());
        existing.setCompanyDescription(dto.getCompanyDescription());
        existing.setContractType(dto.getContractType());
        existing.setDeadline(dto.getDeadline());
        existing.setDepartment(dto.getDepartment());
        existing.setLocation(dto.getLocation());
        existing.setSalary(dto.getSalary());
        existing.setRemoteDays(dto.getRemoteDays());
        existing.setSkillsRequired(dto.getSkillsRequired());

        return jobOfferMapper.entityToDto(jobOfferRepository.save(existing));
    }

    @Override
    public void deleteJobOffer(Long id) {
        jobOfferRepository.deleteById(id);
    }

    @Override
    public List<JobOfferDTO> searchJobOffers(String keyword) {
        // 1. Récupérer les autorités de l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isPrivileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_RH") || a.getAuthority().equals("ROLE_ADMIN"));

        List<JobOfferEntity> entities;

        if (isPrivileged) {
            // Un RH/Admin voit tout ce qui match le mot-clé
            entities = jobOfferRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        } else {
            // Un candidat/visiteur ne voit que les offres 'OPEN'
            entities = jobOfferRepository.findByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCase(
                    JobStatusEnum.OPEN, keyword,
                    JobStatusEnum.OPEN, keyword
            );
        }

        return jobOfferMapper.entitiesToDtos(entities);
    }
}