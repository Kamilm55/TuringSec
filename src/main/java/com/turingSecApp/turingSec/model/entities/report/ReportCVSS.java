package com.turingSecApp.turingSec.model.entities.report;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "report_cvss")
public class ReportCVSS extends Report {
    private String attackVector;
    private String attackComplexity;
    private String privilegesRequired;
    private String userInteractions;
    private String scope;
    private String confidentiality;
    private String integrity;
    private String availability;

    private Double score;

    // Constructors, getters, and setters
}

