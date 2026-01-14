package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.users.UserEntity;
import edu.miage.springboot.dao.entities.users.UserRoleEntity;
import edu.miage.springboot.web.dtos.users.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "mapRoles")
    @Mapping(source = "employeProfile.id", target = "employeProfileId")
    @Mapping(source = "referentEmploye.id", target = "referentId")
    UserDTO toDto(UserEntity entity);

    List<UserDTO> toDtos(List<UserEntity> entities);

    /**
     * Convertit le Set de UserRoleEntity en Set de String (noms des rôles)
     * ex: [RoleEntity(name="ROLE_ADMIN")] -> ["ROLE_ADMIN"]
     */
    @Named("mapRoles")
    default Set<String> mapRoles(Set<UserRoleEntity> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(UserRoleEntity::getName)
                .collect(Collectors.toSet());
    }
}