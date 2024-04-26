package com.turingSecApp.turingSec.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorWithIdPayload {
    //  private Long bugBountyReportId;
    private Long id;
    private String hackerUsername;
    private Double collaborationPercentage;
}
