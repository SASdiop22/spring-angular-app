package edu.miage.springboot.utils.mappers;

import edu.miage.springboot.dao.entities.offers.ApplicationEntity;
import edu.miage.springboot.dao.entities.offers.ApplicationNoteEntity;
import edu.miage.springboot.web.dtos.offers.ApplicationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { ApplicationNoteMapper.class }) // Ajout du "uses"
public interface ApplicationMapper {

    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "candidate.user.username", target = "candidateName")
    @Mapping(source = "job.id", target = "jobOfferId")
    @Mapping(source = "job.title", target = "jobOfferTitle")
    @Mapping(source = "currentStatus", target = "status")
    @Mapping(source = "recruitmentNotes", target = "notes") // Mappe la List<Entity> vers List<DTO>
    ApplicationDTO toDto(ApplicationEntity entity);

    List<ApplicationDTO> toDtos(List<ApplicationEntity> all);
}