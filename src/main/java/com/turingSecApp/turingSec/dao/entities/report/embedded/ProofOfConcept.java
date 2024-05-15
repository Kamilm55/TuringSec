package com.turingSecApp.turingSec.dao.entities.report.embedded;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProofOfConcept {
    @NotBlank()
    private String title;
//    @NotBlank()
    private String vulnerabilityUrl;
    @NotBlank()
    private String description;
    // List of attachments such as images and videos
//    private List<String> attachments;
}
