package com.turingSecApp.turingSec.dao.entities.report;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "report_manual")
public class ReportManual extends ReportEntity{
    private String severity;
}
