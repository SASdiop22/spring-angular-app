package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.users.CandidatEntity;
import edu.miage.springboot.web.dtos.users.CandidatDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidatMapper {

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.username", target = "username")
    @Mapping(target = "rgpdCompliant", expression = "java(entity.isRgpdCompliant())")
    CandidatDTO toDto(CandidatEntity entity);

    @Mapping(target = "user", ignore = true) // Le User est généralement géré par le service Auth
    @Mapping(target = "applications", ignore = true)
    CandidatEntity toEntity(CandidatDTO dto);

    List<CandidatDTO> toDtos(List<CandidatEntity> entities);
}