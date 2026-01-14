package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.dao.entities.offers.JobStatusEnum;
import edu.miage.springboot.dao.repositories.users.EmployeRepository;
import edu.miage.springboot.dao.repositories.offers.JobOfferRepository;
import edu.miage.springboot.services.interfaces.JobOfferService;
import edu.miage.springboot.utils.mappers.JobOfferMapper;
import edu.miage.springboot.web.dtos.offers.JobOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobOfferServiceImpl implements JobOfferService {

    @Autowired
    private JobOfferRepository jobOfferRepository;

    @Autowired
    private JobOfferMapper jobOfferMapper;

    @Autowired
    private EmployeRepository employeRepository; // Nécessaire pour lier le créateur

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
        // On ne récupère que les offres dont le statut est OPEN
        List<JobOfferEntity> entities = jobOfferRepository.findByStatus(JobStatusEnum.OPEN);
        return jobOfferMapper.entitiesToDtos(entities);
    }

    // --- Spec 2.A : Enrichissement et Publication par RH ---
    @Override
    @Transactional
    public JobOfferDTO enrichAndPublish(Long id, Double salary, Integer remoteDays) {
        JobOfferEntity entity = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // Sécurité métier : On ne publie que ce qui est en attente (PENDING) ou en brouillon (DRAFT)
        if (entity.getStatus() == JobStatusEnum.CLOSED || entity.getStatus() == JobStatusEnum.FILLED) {
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
                if (jobOffer.getSalaryRange() == null) {
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
        JobOfferEntity entity = jobOfferMapper.dtoToEntity(jobOfferDTO);

        // Liaison avec le créateur (Demandeur de poste)
        if (jobOfferDTO.getCreatorId() != null) {
            EmployeEntity creator = employeRepository.findById(jobOfferDTO.getCreatorId())
                    .orElseThrow(() -> new RuntimeException("Employé créateur introuvable"));
            entity.setCreator(creator);
        }

        entity.setStatus(JobStatusEnum.DRAFT);
        return jobOfferMapper.entityToDto(jobOfferRepository.save(entity));
    }

    /**
     * Conforme à la Spec 2.A : Validation et Publication par les RH
     */
    @Transactional
    public JobOfferDTO publishOffer(Long id, Double salary, Integer remoteDays) {
        JobOfferEntity entity = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // Utilise la méthode métier interne de l'entité
        entity.validateAndPublish(salary, remoteDays);

        return jobOfferMapper.entityToDto(jobOfferRepository.save(entity));
    }

    @Override
    @Transactional
    public JobOfferDTO updateJobOffer(Long id, JobOfferDTO dto) {
        JobOfferEntity existing = jobOfferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setDeadline(dto.getDeadline());
        existing.setDepartment(dto.getDepartment());
        existing.setSkillsRequired(dto.getSkillsRequired());
        existing.setLocation(dto.getLocation());
        // Note: Le salaire et le statut sont généralement gérés par des méthodes dédiées (workflow)

        return jobOfferMapper.entityToDto(jobOfferRepository.save(existing));
    }

    @Override
    public void deleteJobOffer(Long id) {
        jobOfferRepository.deleteById(id);
    }

    @Override
    public List<JobOfferDTO> searchJobOffers(String keyword) {
        List<JobOfferEntity> entities = jobOfferRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        return jobOfferMapper.entitiesToDtos(entities);
    }
}