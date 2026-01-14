package edu.miage.springboot.services.impl.users;

import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.dao.repositories.users.CandidatRepository;
import edu.miage.springboot.services.interfaces.CandidatService;
import edu.miage.springboot.utils.mappers.CandidatMapper;
import edu.miage.springboot.web.dtos.users.CandidatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CandidatServiceImpl implements CandidatService {

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private CandidatMapper candidatMapper;

    @Override
    public List<CandidatDTO> findAll() {
        return candidatMapper.toDtos(candidatRepository.findAll());
    }

    @Override
    public CandidatDTO findById(Long id) {
        CandidatEntity candidat = candidatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable avec l'id : " + id));
        return candidatMapper.toDto(candidat);
    }

    @Override
    @Transactional
    public CandidatDTO updateProfile(Long id, CandidatDTO dto) {
        CandidatEntity entity = candidatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable"));

        // Mise à jour des informations de contact
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setTelephone(dto.getTelephone());
        entity.setVille(dto.getVille());

        return candidatMapper.toDto(candidatRepository.save(entity));
    }

    /**
     * Spécification 3.A : Renouvellement du consentement RGPD.
     * Cette méthode permet au candidat de réinitialiser le compteur des 2 ans.
     */
    @Override
    @Transactional
    public void renewConsent(Long id) {
        CandidatEntity entity = candidatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable"));

        entity.setConsentDate(LocalDateTime.now());
        candidatRepository.save(entity);
    }

    @Override
    @Transactional
    public void deleteCandidat(Long id) {
        candidatRepository.deleteById(id);
    }

    @Override
    public CandidatDTO findByUsername(String username) {
        CandidatEntity candidat = candidatRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Candidat introuvable avec l'id : " + username));
        return candidatMapper.toDto(candidat);
    }
}