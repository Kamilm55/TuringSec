package com.turingSecApp.turingSec.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BugBountyProgramWithAssetTypePayload {
    // private Long id; // New field for company ID

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;
    @NotBlank(message = "Policy is required")
    private String policy;
    private String notes;
//    @AllFieldsRequired(message = "Asset types must have all fields populated") //todo: do this it's in chatgpt
    private List<AssetTypePayload> assetTypes = new ArrayList<>();

//    @AllFieldsRequired(message = "Prohibits must have all fields populated")
    private List<StrictPayload> prohibits = new ArrayList<>();

    @NotNull(message = "Company Id is required")
    private Long companyId;
    @NotNull(message = "Program Id is required")
    private Long programId;

    // Getters and setters
}
