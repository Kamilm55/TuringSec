package com.turingSecApp.turingSec.payload.program;

import com.turingSecApp.turingSec.dao.entities.program.asset.child.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import lombok.*;

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
