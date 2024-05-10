package com.turingSecApp.turingSec.response.report;

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
    private String hackerUsername;
    private Double collaborationPercentage;
}
