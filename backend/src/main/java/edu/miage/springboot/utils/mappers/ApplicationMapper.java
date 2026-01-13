package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.ApplicationEntity;
import edu.miage.springboot.web.dtos.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "candidate.username", target = "candidateName")
    @Mapping(source = "job.id", target = "jobOfferId")
    @Mapping(source = "job.title", target = "jobOfferTitle")
    @Mapping(source = "currentStatus", target = "status")
    ApplicationDTO toDto(ApplicationEntity entity);

    List<ApplicationDTO> toDtos(List<ApplicationEntity> entities);
}