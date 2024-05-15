package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.dao.entities.report.ReportEntity;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "bugBountyProgram.id", target = "bugBountyProgramId")
    @Mapping(source = "asset", target = "reportAsset")
     //@Mapping(target = "collaborators", expression = "java(reportsEntity.getCollaborators() != null ? new java.util.ArrayList<>(reportsEntity.getCollaborators()) : null)")
    ReportDTO toDTO(ReportEntity reportEntity);

}
