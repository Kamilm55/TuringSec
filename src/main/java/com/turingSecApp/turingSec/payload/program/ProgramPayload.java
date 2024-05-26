package com.turingSecApp.turingSec.payload.program;

import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProgramPayload {
    // private Long id; // New field for company ID

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;
    @NotBlank(message = "Policy is required")
    private String policy;
    private String notes;
    private List<@Valid StrictPayload> prohibits = new ArrayList<>();

    private List<String> inScope = new ArrayList<>();

    private List<String> outOfScope = new ArrayList<>();

    private @Valid ProgramAssetPayload asset;
//    @NotNull(message = "Company Id is required")
//    private Long companyId;
//    @NotNull(message = "Program Id is required")
//    private Long programId;

    // Getters and setters
}
