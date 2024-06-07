package com.turingSecApp.turingSec.model.entities.program;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.model.entities.program.asset.child.BaseProgramAsset;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

//@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "baseProgramAsset")
@ToString(exclude = "baseProgramAsset")
@Builder
@Entity
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @ElementCollection
    private Set<String> names = new HashSet<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "base_program_asset_id")
    @JsonIgnore
    private BaseProgramAsset baseProgramAsset;
}
