package com.turingSecApp.turingSec.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugBountyReportDTO {
    private Long id;
    private Long userId; // User ID associated with the report
//TEST
}
