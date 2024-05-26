package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.dao.entities.user.HackerEntity;
import com.turingSecApp.turingSec.response.user.HackerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HackerMapper {

    HackerMapper INSTANCE = Mappers.getMapper(HackerMapper.class);
    @Mappings({
            @Mapping(target = "userId", source = "hackerEntity.user.id"),
            // set bg img id and img id explicitly
    })
    HackerDTO convert(HackerEntity hackerEntity);
}
