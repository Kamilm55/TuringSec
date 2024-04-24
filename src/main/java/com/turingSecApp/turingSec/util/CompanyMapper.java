package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.response.CompanyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    //@Mapping(target = "bugBountyPrograms_id", source = "bugBountyPrograms", qualifiedByName = "mapBugBountyProgramsIds")
    CompanyDTO convert(CompanyEntity companyEntity);
}
