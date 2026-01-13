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
    @Mapping(source = "jobOffer.id", target = "jobOfferId")
    @Mapping(source = "jobOffer.title", target = "jobOfferTitle")
    @Mapping(source = "cv.id", target = "cvId")
    ApplicationDTO toDto(ApplicationEntity entity);

    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "jobOffer", ignore = true)
    @Mapping(target = "cv", ignore = true)
    ApplicationEntity toEntity(ApplicationDTO dto);

    List<ApplicationDTO> toDtos(List<ApplicationEntity> entities);
}