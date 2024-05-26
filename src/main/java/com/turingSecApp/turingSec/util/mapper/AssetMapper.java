package com.turingSecApp.turingSec.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AssetMapper {
    AssetMapper INSTANCE = Mappers.getMapper(AssetMapper.class);

//    ProgramAsset fromProgramAssetPayloadToProgramAsset(ProgramAssetPayload programAssetPayload);
}
