package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.FolderEntity;
import edu.miage.springboot.web.dtos.FolderDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FolderMapper {
    FolderEntity dtoToEntity(FolderDTO dto);
    FolderDTO entityToDto(FolderEntity entity);
    List<FolderEntity> dtosToEntities(List<FolderDTO> dtos);
    List<FolderDTO> entitiesToDtos(List<FolderEntity> entities);
}
