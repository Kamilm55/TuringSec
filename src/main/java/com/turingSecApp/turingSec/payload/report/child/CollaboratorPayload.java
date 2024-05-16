package com.turingSecApp.turingSec.payload.report.child;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorPayload {
    /*private Long id*//*bugBountyReportId*/;
    @Schema(example = "Username")
    private String hackerUsername;
    private Double collaborationPercentage;
}
