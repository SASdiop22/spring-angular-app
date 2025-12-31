package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.FolderEntity;
import edu.miage.springboot.web.dtos.FolderDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-30T16:43:22+0100",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class FolderMapperImpl implements FolderMapper {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public FolderEntity dtoToEntity(FolderDTO dto) {
        if ( dto == null ) {
            return null;
        }

        FolderEntity folderEntity = new FolderEntity();

        folderEntity.setFiles( fileMapper.dtosToEntities( dto.getFiles() ) );
        folderEntity.setId( dto.getId() );
        folderEntity.setName( dto.getName() );

        return folderEntity;
    }

    @Override
    public FolderDTO entityToDto(FolderEntity entity) {
        if ( entity == null ) {
            return null;
        }

        FolderDTO folderDTO = new FolderDTO();

        folderDTO.setId( entity.getId() );
        folderDTO.setName( entity.getName() );
        folderDTO.setFiles( fileMapper.entitiesToDtos( entity.getFiles() ) );

        return folderDTO;
    }

    @Override
    public List<FolderEntity> dtosToEntities(List<FolderDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<FolderEntity> list = new ArrayList<FolderEntity>( dtos.size() );
        for ( FolderDTO folderDTO : dtos ) {
            list.add( dtoToEntity( folderDTO ) );
        }

        return list;
    }

    @Override
    public List<FolderDTO> entitiesToDtos(List<FolderEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<FolderDTO> list = new ArrayList<FolderDTO>( entities.size() );
        for ( FolderEntity folderEntity : entities ) {
            list.add( entityToDto( folderEntity ) );
        }

        return list;
    }
}
