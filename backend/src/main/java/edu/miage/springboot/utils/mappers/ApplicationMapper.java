
package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    // Conversion Entity -> DTO
    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "candidate.user.username", target = "candidateName")
    @Mapping(source = "job.id", target = "jobOfferId")
    @Mapping(source = "job.title", target = "jobOfferTitle")
    @Mapping(source = "currentStatus", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "createdAt", target = "applicationDate")
    ApplicationDTO toDto(ApplicationEntity entity);

    // Conversion Entity -> DTO (liste)
    List<ApplicationDTO> toDtos(List<ApplicationEntity> entities);

    // Conversion DTO -> Entity (pour création/mise à jour)
    @Mapping(target = "candidate", ignore = true)  // Géré manuellement dans le service
    @Mapping(target = "job", ignore = true)        // Géré manuellement dans le service
    @Mapping(source = "status", target = "currentStatus")
    @Mapping(target = "createdAt", ignore = true)  // Géré par @PrePersist
    ApplicationEntity toEntity(ApplicationDTO dto);
}