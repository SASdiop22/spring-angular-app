package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "candidate.user.username", target = "candidateName")
    @Mapping(source = "job.id", target = "jobOfferId")
    @Mapping(source = "job.title", target = "jobOfferTitle")
    @Mapping(source = "currentStatus", target = "status")
        // MapStruct fera le mapping automatique pour meetingDate, rejectionReason et recruitmentNotes
        // si les noms sont identiques dans l'Entity et le DTO.
    ApplicationDTO toDto(ApplicationEntity entity);

    List<ApplicationDTO> toDtos(List<ApplicationEntity> entities);
}