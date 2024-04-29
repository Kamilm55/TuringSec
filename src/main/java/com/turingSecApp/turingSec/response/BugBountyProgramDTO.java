package com.turingSecApp.turingSec.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.AssetTypeEntity;
import com.turingSecApp.turingSec.dao.entities.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.StrictEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "assetTypes", "prohibits"})
@ToString(exclude = {"assetTypes", "prohibits"})
@Builder
public class BugBountyProgramDTO {
    private Long id;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String notes;
    private String policy;

    //private Long companyId;

    private List<AssetTypeEntity> assetTypes = new ArrayList<>();

    private List<StrictEntity> prohibits = new ArrayList<>();
}
