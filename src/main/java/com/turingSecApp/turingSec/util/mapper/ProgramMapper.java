package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.response.BugBountyProgramWithAssetTypeDTO;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProgramMapper {
    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    @Mapping(target = "inScope", expression = "java(programEntity.getInScope() != null ? new ArrayList<>(programEntity.getInScope()) : null)")
    @Mapping(target = "outOfScope", expression = "java(programEntity.getOutOfScope() != null ? new ArrayList<>(programEntity.getOutOfScope()) : null)")
    BugBountyProgramDTO toDto(BugBountyProgramEntity programEntity);

    @Mapping(source = "company.id", target = "companyId")
    BugBountyProgramWithAssetTypeDTO toDTO(BugBountyProgramEntity entity);
}
