package com.turingSecApp.turingSec.payload.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReportDateRangeRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
