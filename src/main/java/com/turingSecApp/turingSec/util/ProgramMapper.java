package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.Request.BugBountyProgramWithAssetTypeDTO;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.response.BugBountyProgramDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProgramMapper {
    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    BugBountyProgramDTO toDto(BugBountyProgramEntity programEntity);

    @Mapping(source = "company.id", target = "companyId")
    BugBountyProgramWithAssetTypeDTO toDTO(BugBountyProgramEntity entity);
}
