package com.turingSecApp.turingSec.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AssetTypeDTO {
   // private Long id;
    private String level;
    private String assetType;
    private Double price;
    private Long programId; // New field for program ID

    // Getters and setters
}
