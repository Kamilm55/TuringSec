package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.model.entities.user.UserEntityI;
import com.turingSecApp.turingSec.response.user.UserDTO;
import com.turingSecApp.turingSec.model.entities.user.AdminEntity;
import com.turingSecApp.turingSec.model.entities.user.HackerEntity;
import com.turingSecApp.turingSec.response.admin.AdminDTO;
import com.turingSecApp.turingSec.response.user.UserHackerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "username", source = "userEntity.username")
    @Mapping(target = "first_name", source = "userEntity.first_name")
    @Mapping(target = "last_name", source = "userEntity.last_name")
    @Mapping(target = "country", source = "userEntity.country")
    @Mapping(target = "website", source = "hackerEntity.website")
    @Mapping(target = "has_background_pic", source = "hackerEntity.has_background_pic")
    @Mapping(target = "has_profile_pic", source = "hackerEntity.has_profile_pic")
    @Mapping(target = "bio", source = "hackerEntity.bio")
    @Mapping(target = "linkedin", source = "hackerEntity.linkedin")
    @Mapping(target = "twitter", source = "hackerEntity.twitter")
    @Mapping(target = "github", source = "hackerEntity.github")
    @Mapping(target = "city", source = "hackerEntity.city")
    @Mapping(target = "hackerId", source = "hackerEntity.id") // Add this mapping for hackerId
    @Mapping(target = "userId", source = "userEntity.id") // Add this mapping for userId
    UserHackerDTO toDto(UserEntityI userEntity, HackerEntity hackerEntity);

    @Mappings({
            @Mapping(source = "first_name", target = "firstName"),
            @Mapping(source = "last_name", target = "lastName"),
            @Mapping(source = "hacker.id", target = "hackerId")
            // Add more mappings as needed
    })
    UserDTO convert(UserEntityI userEntity);

//    UserDTOWithCity convert(UserEntity userEntity , HackerEntity hackerEntity);
    AdminDTO convert(AdminEntity adminEntity);
}

