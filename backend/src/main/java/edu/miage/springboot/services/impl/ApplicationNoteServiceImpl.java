package edu.miage.springboot.services.impl;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationNoteEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.offers.ApplicationNoteRepository;
import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.web.dtos.offers.ApplicationNoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationNoteServiceImpl{

    @Autowired private ApplicationNoteRepository noteRepository;
    @Autowired private ApplicationRepository applicationRepository;
    @Autowired private UserRepository userRepository;

    public List<ApplicationNoteDTO> getNotesByApplication(Long applicationId) {
        return noteRepository.findByApplicationIdOrderByCreatedAtAsc(applicationId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ApplicationNoteDTO addNote(Long applicationId, ApplicationNoteDTO dto) {
        ApplicationEntity app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));

        // Récupération de l'utilisateur connecté via Spring Security
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ApplicationNoteEntity note = new ApplicationNoteEntity();
        note.setApplication(app);
        note.setAuthor(currentUser);
        note.setStepName(dto.getStepName());
        note.setContent(dto.getContent());
        note.setCreatedAt(LocalDateTime.now());

        return convertToDto(noteRepository.save(note));
    }

    // Méthode de mapping interne (Peut être remplacée par MapStruct)
    private ApplicationNoteDTO convertToDto(ApplicationNoteEntity entity) {
        ApplicationNoteDTO dto = new ApplicationNoteDTO();
        dto.setId(entity.getId());
        dto.setApplicationId(entity.getApplication().getId());
        dto.setContent(entity.getContent());
        dto.setStepName(entity.getStepName());
        dto.setCreatedAt(entity.getCreatedAt());
        // On récupère le nom complet de l'auteur pour l'affichage
        dto.setAuthorName(entity.getAuthor().getPrenom() + " " + entity.getAuthor().getNom());
        return dto;
    }
}