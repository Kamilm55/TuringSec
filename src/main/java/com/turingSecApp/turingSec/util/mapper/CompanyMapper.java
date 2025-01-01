package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.payload.company.CompanyUpdateRequest;
import com.turingSecApp.turingSec.response.company.CompanyDTO;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    @Mapping(source = "bugBountyPrograms", target = "bugBountyPrograms_id")
    CompanyDTO toDto(CompanyEntity companyEntity);

    @Mappings({
            @Mapping(source = "bugBountyPrograms", target = "bugBountyPrograms")
    })
    CompanyResponse convertToResponse(CompanyEntity companyEntity);

    void mapForUpdate(@MappingTarget CompanyEntity company, CompanyUpdateRequest updateRequest);


    default Set<Long> mapBugBountyPrograms(Set<Program> bugBountyPrograms) {
        return bugBountyPrograms.stream()
                .map(Program::getId)
                .collect(Collectors.toSet());
    }
}
