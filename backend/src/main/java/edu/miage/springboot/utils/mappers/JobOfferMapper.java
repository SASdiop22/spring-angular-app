package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.offers.JobOfferEntity;
import edu.miage.springboot.web.dtos.offers.JobOfferDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
@Mapper(componentModel = "spring")
public interface JobOfferMapper {
    @Mapping(source = "creator.id", target = "creatorId")
    @Mapping(source = "creator.user.username", target = "creatorName") // Pour avoir le nom
    JobOfferDTO entityToDto(JobOfferEntity entity);
    JobOfferEntity dtoToEntity(JobOfferDTO dto);
    List<JobOfferDTO> entitiesToDtos(List<JobOfferEntity> entities);
    List<JobOfferEntity> dtosToEntities(List<JobOfferDTO> dtos);

}
