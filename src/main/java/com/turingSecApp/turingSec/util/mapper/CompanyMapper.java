package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.response.CompanyDTO;
import com.turingSecApp.turingSec.response.CompanyResponse;
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

    default Set<Long> mapBugBountyPrograms(Set<BugBountyProgramEntity> bugBountyPrograms) {
        return bugBountyPrograms.stream()
                .map(BugBountyProgramEntity::getId)
                .collect(Collectors.toSet());
    }
}
