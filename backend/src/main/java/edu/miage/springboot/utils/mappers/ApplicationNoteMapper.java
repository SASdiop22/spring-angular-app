package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.offers.ApplicationNoteEntity;
import edu.miage.springboot.web.dtos.offers.ApplicationNoteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationNoteMapper {

    /**
     * Mappe une entité Note vers son DTO.
     * On extrait l'ID de la candidature et le nom de l'auteur pour aplatir l'objet.
     */
    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "author.username", target = "authorName")
    ApplicationNoteDTO toDto(ApplicationNoteEntity entity);

    /**
     * Permet de mapper une liste complète de notes (utile pour le journal de recrutement).
     */
    List<ApplicationNoteDTO> toDtos(List<ApplicationNoteEntity> entities);
}