package com.turingSecApp.turingSec.response.program;

import com.turingSecApp.turingSec.model.entities.program.StrictEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "prohibits"})
@ToString(exclude = {"prohibits"})
@Builder
public class BugBountyProgramDTO {
    private Long id;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String notes;
    private String policy;

    //private Long companyId;

    private List<String> inScope = new ArrayList<>();

    private List<String> outOfScope = new ArrayList<>();

//    private List<AssetTypeEntity> assetTypes = new ArrayList<>();

    private List<StrictEntity> prohibits = new ArrayList<>();
}
