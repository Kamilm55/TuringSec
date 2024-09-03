package com.turingSecApp.turingSec.response.program;

import com.turingSecApp.turingSec.model.entities.program.Prohibit;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
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
public class ProgramDTO {

    // Company entity fields
    private String companyId;
    private String companyName;

    // Program entity fields
    private Long id;

    private LocalDate fromDate;
    private LocalDate toDate;
    private Long lastDays;
    private String notes;
    private String policy;

    private List<String> inScope = new ArrayList<>();

    private List<String> outOfScope = new ArrayList<>();
    private List<Prohibit> prohibits = new ArrayList<>();

    private ProgramAsset asset;
}
