package edu.miage.springboot.services.impl.offers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationNoteEntity;
import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.repositories.offers.ApplicationNoteRepository;
import edu.miage.springboot.dao.repositories.offers.ApplicationRepository;
import edu.miage.springboot.dao.repositories.users.UserRepository;
import edu.miage.springboot.utils.mappers.ApplicationNoteMapper;
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
    @Autowired private ApplicationNoteMapper noteMapper;

    public List<ApplicationNoteDTO> getNotesByApplication(Long applicationId) {
        return noteMapper.toDtos(noteRepository.findByApplicationIdOrderByCreatedAtAsc(applicationId));
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

        return noteMapper.toDto(noteRepository.save(note));
    }

}