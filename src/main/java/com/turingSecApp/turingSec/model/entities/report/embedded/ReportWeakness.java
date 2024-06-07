package com.turingSecApp.turingSec.model.entities.report.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportWeakness {

    @NotBlank(message = "Weakness type is required")
    private String type;

    @NotBlank(message = "Weakness name is required")
    private String name;
}

