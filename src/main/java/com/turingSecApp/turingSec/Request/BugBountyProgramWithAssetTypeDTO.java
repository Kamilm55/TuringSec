package com.turingSecApp.turingSec.Request;

import com.turingSecApp.turingSec.payload.AssetTypePayload;
import com.turingSecApp.turingSec.payload.StrictPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BugBountyProgramWithAssetTypeDTO {
   // private Long id; // New field for company ID
    private LocalDate fromDate;
    private LocalDate toDate;
    private String notes;
    private String policy;
    private List<AssetTypeDTO> assetTypes;
    private List<StrictDTO> prohibits; // New field for prohibits
    private Long companyId; // New field for company ID
    private Long programId;

    // Getters and setters
}