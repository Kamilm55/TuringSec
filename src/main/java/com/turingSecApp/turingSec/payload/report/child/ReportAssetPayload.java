package com.turingSecApp.turingSec.payload.report.child;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportAssetPayload {
    @NotBlank(message = "Asset name is required")
    private String assetName;
    @NotBlank(message = "Asset type is required")
    private String assetType;
}
