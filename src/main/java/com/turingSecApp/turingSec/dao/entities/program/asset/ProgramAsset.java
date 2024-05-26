package com.turingSecApp.turingSec.dao.entities.program.asset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.program.asset.child.*;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(exclude = {"program"})
@ToString(exclude = {"program"})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "program_assets")
public class ProgramAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "programAsset",cascade = CascadeType.ALL,orphanRemoval = true)
    private LowProgramAsset lowAsset;

    @OneToOne(mappedBy = "programAsset",cascade = CascadeType.ALL,orphanRemoval = true)
    private MediumProgramAsset mediumAsset;

    @OneToOne(mappedBy = "programAsset",cascade = CascadeType.ALL,orphanRemoval = true)
    private HighProgramAsset highAsset;

    @OneToOne(mappedBy = "programAsset",cascade = CascadeType.ALL,orphanRemoval = true)
    private CriticalProgramAsset criticalAsset;

    //
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_bounty_program_id")
    @JsonIgnore
    private Program program;


    // Getters and setters
}
