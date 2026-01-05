package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.*;
import edu.miage.springboot.dao.repositories.*;
import edu.miage.springboot.services.interfaces.ApplicationService;
import edu.miage.springboot.utils.mappers.ApplicationMapper;
import edu.miage.springboot.web.dtos.ApplicationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobOfferRepository jobOfferRepository;
    @Autowired
    private FileRepository fileRepository;
    private ApplicationMapper applicationMapper;

    @Override
    @Transactional // Très important pour garantir que tout est sauvegardé correctement
    public void apply(Long jobOfferId, Long candidateId, MultipartFile cvFile) {
        // 1. Récupérer le candidat et l'offre depuis la BDD
        UserEntity candidate = userRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable"));

        JobOfferEntity jobOffer = jobOfferRepository.findById(jobOfferId)
                .orElseThrow(() -> new RuntimeException("Offre introuvable"));

        // 2. Gérer le fichier du CV (Logique simplifiée)
        FileEntity cvEntity = new FileEntity();
        cvEntity.setName(cvFile.getOriginalFilename());
        // Ici, tu devrais normalement sauvegarder le fichier sur le disque
        cvEntity = fileRepository.save(cvEntity);

        // 3. Créer et configurer la candidature
        ApplicationEntity application = new ApplicationEntity();
        application.setCandidate(candidate);
        application.setJobOffer(jobOffer);
        application.setCv(cvEntity);
        // La date et le statut sont déjà gérés dans le constructeur de l'entité

        // 4. Sauvegarder la candidature
        applicationRepository.save(application);
    }

    @Override
    public List<ApplicationDTO> findAll() {
        // On récupère toutes les entités et on les transforme en DTO d'un coup
        List<ApplicationEntity> entities = applicationRepository.findAll();
        return applicationMapper.toDtos(entities);
    }
}