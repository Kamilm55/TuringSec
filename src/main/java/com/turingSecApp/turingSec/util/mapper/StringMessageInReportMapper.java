package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.message.StringMessageInReport;
import com.turingSecApp.turingSec.response.message.StringMessageInReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StringMessageInReportMapper {
    StringMessageInReportMapper INSTANCE = Mappers.getMapper(StringMessageInReportMapper.class);

    @Mapping(target = "reportId" , source = "report.id")
    @Mapping(target = "replyToId" , source = "replyTo.id")
    StringMessageInReportDTO toDTO(StringMessageInReport stringMessageInReport);
}
