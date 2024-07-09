package com.turingSecApp.turingSec.response.program;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.model.entities.program.StrictEntity;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    private Long companyId;
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
    private List<StrictEntity> prohibits = new ArrayList<>();

    private ProgramAsset asset;
}
