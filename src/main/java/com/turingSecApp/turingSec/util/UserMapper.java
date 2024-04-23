package com.turingSecApp.turingSec.util;

import com.turingSecApp.turingSec.Request.UserDTO;
import com.turingSecApp.turingSec.dao.entities.HackerEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import com.turingSecApp.turingSec.response.UserHackerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "username", source = "userEntity.username")
    @Mapping(target = "first_name", source = "userEntity.first_name")
    @Mapping(target = "last_name", source = "userEntity.last_name")
    @Mapping(target = "country", source = "userEntity.country")
    @Mapping(target = "website", source = "hackerEntity.website")
    @Mapping(target = "background_pic", source = "hackerEntity.background_pic")
    @Mapping(target = "profile_pic", source = "hackerEntity.profile_pic")
    @Mapping(target = "bio", source = "hackerEntity.bio")
    @Mapping(target = "linkedin", source = "hackerEntity.linkedin")
    @Mapping(target = "twitter", source = "hackerEntity.twitter")
    @Mapping(target = "github", source = "hackerEntity.github")
    @Mapping(target = "city", source = "hackerEntity.city")
    UserHackerDTO toDto(UserEntity userEntity, HackerEntity hackerEntity);

    @Mapping(target = "firstName", source = "userEntity.first_name")
    @Mapping(target = "lastName", source = "userEntity.last_name")
    UserDTO convert(UserEntity userEntity);
}

