package com.turingSecApp.turingSec.model.entities.program.asset.child;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@NoArgsConstructor
public class MediumProgramAsset extends BaseProgramAsset {
}
