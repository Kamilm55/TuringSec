package com.turingSecApp.turingSec.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollaboratorDTO {
  //  private Long bugBountyReportId;
    private String hackerUsername;
    private Double collaborationPercentage;
}
