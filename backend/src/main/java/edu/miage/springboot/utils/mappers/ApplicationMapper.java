package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { ApplicationNoteMapper.class })
public interface ApplicationMapper {

    // Conversion Entity -> DTO
    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "candidate.user.username", target = "candidateName")
    @Mapping(source = "job.id", target = "jobOfferId")
    @Mapping(source = "job.title", target = "jobOfferTitle")
    @Mapping(source = "currentStatus", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "createdAt", target = "applicationDate")
    @Mapping(source = "recruitmentNotes", target = "notes") // Support pour la Spec 4.B
    ApplicationDTO toDto(ApplicationEntity entity);

    // Conversion Entity -> DTO (liste)
    List<ApplicationDTO> toDtos(List<ApplicationEntity> entities);

    // Conversion DTO -> Entity (pour création/mise à jour)
    @Mapping(target = "candidate", ignore = true)  // Géré manuellement dans le service
    @Mapping(target = "job", ignore = true)        // Géré manuellement dans le service
    @Mapping(source = "status", target = "currentStatus")
    @Mapping(target = "createdAt", ignore = true)  // Géré par @PrePersist
    @Mapping(source = "notes", target = "recruitmentNotes")
    ApplicationEntity toEntity(ApplicationDTO dto);
}