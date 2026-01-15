package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.users.EmployeEntity;
import edu.miage.springboot.web.dtos.users.EmployeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeMapper {
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    // Gère la concaténation nomComplet automatiquement
    @Mapping(target = "nomComplet", expression = "java(entity.getUser().getPrenom() + \" \" + entity.getUser().getNom())")
    // Mapping du référent
    @Mapping(source = "referent.id", target = "referentId")
    @Mapping(target = "referentNomComplet", expression = "java(entity.getReferent() != null ? entity.getReferent().getUser().getPrenom() + \" \" + entity.getReferent().getUser().getNom() : null)")

    EmployeDTO toDto(EmployeEntity entity);

    List<EmployeDTO> toDtos(List<EmployeEntity> entities);
}