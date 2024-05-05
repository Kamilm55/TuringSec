package com.turingSecApp.turingSec.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssetTypePayload {
    @NotBlank(message = "Level is required")
    private String level;

    @NotBlank(message = "Asset type is required")
    private String assetType;

    @NotBlank(message = "Price is required")
    private String price;
}
