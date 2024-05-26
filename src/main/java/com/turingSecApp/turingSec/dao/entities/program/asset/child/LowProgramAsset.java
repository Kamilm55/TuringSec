package com.turingSecApp.turingSec.dao.entities.program.asset.child;


import com.turingSecApp.turingSec.dao.entities.program.asset.child.BaseProgramAsset;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embeddable;
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
