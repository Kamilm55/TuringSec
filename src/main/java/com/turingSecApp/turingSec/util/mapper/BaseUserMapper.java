package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.user.BaseUser;
import com.turingSecApp.turingSec.response.BaseUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BaseUserMapper {
    BaseUserMapper INSTANCE = Mappers.getMapper(BaseUserMapper.class);

    BaseUserDTO toDTO(BaseUser baseUser);
}
