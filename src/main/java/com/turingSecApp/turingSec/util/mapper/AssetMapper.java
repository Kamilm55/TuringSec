package com.turingSecApp.turingSec.util.mapper;

import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.payload.program.ProgramAssetPayload;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AssetMapper {
    AssetMapper INSTANCE = Mappers.getMapper(AssetMapper.class);

//    ProgramAsset fromProgramAssetPayloadToProgramAsset(ProgramAssetPayload programAssetPayload);
}
