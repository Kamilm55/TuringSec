package com.turingSecApp.turingSec.payload.program.asset;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProgramAssetPayload {

    private @Valid BaseProgramAssetPayload lowAsset;

    private @Valid BaseProgramAssetPayload mediumAsset;

    private @Valid BaseProgramAssetPayload highAsset;

    private @Valid BaseProgramAssetPayload criticalAsset;
}
