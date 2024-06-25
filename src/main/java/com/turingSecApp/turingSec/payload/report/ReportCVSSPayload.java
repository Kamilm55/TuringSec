package com.turingSecApp.turingSec.payload.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReportCVSSPayload extends BugBountyReportPayload{
    @NotBlank(message = "Attack vector is required")
    private String attackVector;
    @NotBlank(message = "Attack complexity is required")
    private String attackComplexity;

    @NotBlank(message = "Privileges required is required")
    private String privilegesRequired;

    @NotBlank(message = "User interactions is required")
    private String userInteractions;

    @NotBlank(message = "Scope is required")
    private String scope;

    @NotBlank(message = "Confidentiality is required")
    private String confidentiality;

    @NotBlank(message = "Integrity is required")
    private String integrity;

    @NotBlank(message = "Availability is required")
    private String availability;

    @NotNull(message = "Cvss score is required")
    private Double score;
}
