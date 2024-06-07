package com.turingSecApp.turingSec.model.entities.program.asset.child;


import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
//@DiscriminatorValue("LOW")  for SINGLE_TABLE
@NoArgsConstructor
public class LowProgramAsset extends BaseProgramAsset {
}
