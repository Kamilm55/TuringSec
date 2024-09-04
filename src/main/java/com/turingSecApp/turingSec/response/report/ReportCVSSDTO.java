package com.turingSecApp.turingSec.response.report;

public class ReportCVSSDTO extends ReportDTO{
    private String attackVector;
    private String attackComplexity;
    private String privilegesRequired;
    private String userInteractions;
    private String scope;
    private String confidentiality;
    private String integrity;
    private String availability;

    private Double score;
}
