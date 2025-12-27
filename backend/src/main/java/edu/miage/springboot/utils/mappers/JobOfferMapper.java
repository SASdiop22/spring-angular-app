package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.JobOfferEntity;
import edu.miage.springboot.web.dtos.JobOfferDTO;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring")
public interface JobOfferMapper {
    JobOfferDTO entityToDto(JobOfferEntity entity);
    JobOfferEntity dtoToEntity(JobOfferDTO dto);
    List<JobOfferDTO> entitiesToDtos(List<JobOfferEntity> entities);
    List<JobOfferEntity> dtosToEntities(List<JobOfferDTO> dtos);
}
