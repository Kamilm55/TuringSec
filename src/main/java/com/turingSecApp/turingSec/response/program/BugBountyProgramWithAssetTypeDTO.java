package com.turingSecApp.turingSec.response.program;

import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
    private Set<ProgramAsset> assets;
    private List<StrictDTO> prohibits; // New field for prohibits
    private Long companyId; // New field for company ID
//    private Long programId; // every company has one program

    // Getters and setters
}