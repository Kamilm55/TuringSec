package com.turingSecApp.turingSec.payload.program.asset;

import com.turingSecApp.turingSec.payload.program.asset.AssetPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BaseProgramAssetPayload {

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be number(double) and greater than 0 or equal to 0")
    private Double price;

    private Set<@Valid AssetPayload> assets = new HashSet<>();
}
