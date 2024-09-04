package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.response.report.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReportMapper {
    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);


}

