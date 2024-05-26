package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.response.company.CompanyDTO;
import com.turingSecApp.turingSec.response.company.CompanyResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
            @Mapping(source = "bugBountyPrograms", target = "bugBountyPrograms"),
          //  @Mapping(source = "roles", target = "roles", ignore = true), // Exclude roles
            @Mapping(source = "userRoles", target = "userRoles", ignore = true) // Exclude userRoles
    })
    CompanyResponse convertToResponse(CompanyEntity companyEntity);

    default Set<Long> mapBugBountyPrograms(Set<Program> bugBountyPrograms) {
        return bugBountyPrograms.stream()
                .map(Program::getId)
                .collect(Collectors.toSet());
    }
}
