package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.response.program.ProgramDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ProgramMapper {
    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.company_name", target = "companyName")
    ProgramDTO toProgramDTO(Program programEntity);

    //Learn:
    // In the context of the ProgramMapper interface, the default keyword is used to provide a custom method implementation directly within the interface. This allows you to add custom logic to MapStruct-generated mappers without needing a separate implementation class.
    // 'default' keyword allows interfaces to have methods with bodies
    @AfterMapping
    default void setElementCollections(Program programEntity, @MappingTarget ProgramDTO programDTO) {
        System.out.println(programEntity.getInScope());

        programDTO.setInScope(programEntity.getInScope());
        programDTO.setOutOfScope(programEntity.getOutOfScope());
    }

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.company_name", target = "companyName")
    List<ProgramDTO> toProgramListDTO(List<Program> programEntity);

//    @Mapping(source = "company.id", target = "companyId")
//    BugBountyProgramWithAssetTypeDTO toDTO(Program entity);
}
