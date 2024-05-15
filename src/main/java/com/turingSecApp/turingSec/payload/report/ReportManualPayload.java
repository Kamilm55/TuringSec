package com.turingSecApp.turingSec.payload.report;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReportManualPayload extends BugBountyReportPayload{
    @NotBlank(message = "Severity is required")
    private String severity;
}
