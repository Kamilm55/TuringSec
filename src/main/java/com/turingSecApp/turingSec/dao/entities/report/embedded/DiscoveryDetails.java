package com.turingSecApp.turingSec.dao.entities.report.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscoveryDetails {
    @NotBlank
    private String timeSpend;
}
