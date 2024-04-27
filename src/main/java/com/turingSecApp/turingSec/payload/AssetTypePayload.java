package com.turingSecApp.turingSec.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssetTypePayload {
    private String level;
    private String assetType;
    private String price;
}
