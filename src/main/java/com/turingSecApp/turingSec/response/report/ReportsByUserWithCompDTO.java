package com.turingSecApp.turingSec.response.report;

import com.turingSecApp.turingSec.dao.entities.report.ReportEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportsByUserWithCompDTO {
    private String companyName;
    private List<ReportEntity> reports;
}
