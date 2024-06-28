package com.turingSecApp.turingSec.model.entities.program.asset.child;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.model.entities.program.Asset;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"programAsset"})
@ToString(exclude = {"programAsset"})
@Entity
@Table(name = "base_program_assets")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // You can use InheritanceType.SINGLE_TABLE or InheritanceType.TABLE_PER_CLASS as well
//@DiscriminatorColumn(name = "asset_type") for SINGLE_TABLE
public class BaseProgramAsset {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "base_program_asset_seq")
    @SequenceGenerator(name = "base_program_asset_seq", sequenceName = "base_program_asset_seq", allocationSize = 1)
    private Long id;

    //private Double price;

    @OneToMany(mappedBy = "baseProgramAsset",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private  Set<Asset>  assets = new HashSet<>();

    @OneToOne // bu 4 denesine aiddir one to one - i pozur
    @JoinColumn(name = "program_asset_id")
//    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private ProgramAsset programAsset;
}
