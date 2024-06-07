package com.turingSecApp.turingSec.helper.entityHelper.report;

import com.turingSecApp.turingSec.model.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.report.ReportManual;
import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.payload.report.child.CollaboratorPayload;

import java.util.List;

public interface IReportEntityHelper {
    Report deleteReportChildEntities(Report report);
    Report createReportsEntityFromPayload(BugBountyReportPayload reportPayload);
    void setCommonReportProperties(Report report, BugBountyReportPayload reportPayload);
    Report setChildReferenceFieldsFromPayload(BugBountyReportPayload reportPayload, Report report);
    void saveCollaborators(List<CollaboratorPayload> collaboratorDTOs, Report report);
    Report saveForReportType(Report report);

    /////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    // manual
    ReportManual createReportManualFromPayload(ReportManualPayload reportPayload);

    // CVSSReport
    ReportCVSS createReportCVSSFromPayload(ReportCVSSPayload reportPayload);
    void setCVSSFields(ReportCVSS reportCVSS,ReportCVSSPayload reportPayload);

    //


}
