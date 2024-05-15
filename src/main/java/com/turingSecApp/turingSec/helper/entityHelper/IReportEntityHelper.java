package com.turingSecApp.turingSec.helper.entityHelper;

import com.turingSecApp.turingSec.dao.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.dao.entities.report.ReportEntity;
import com.turingSecApp.turingSec.dao.entities.report.ReportManual;
import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.payload.report.child.CollaboratorPayload;

import java.util.List;

public interface IReportEntityHelper {
    ReportEntity deleteReportChildEntities(ReportEntity report);
    ReportEntity createReportsEntityFromPayload(BugBountyReportPayload reportPayload);
    void setCommonReportProperties(ReportEntity report, BugBountyReportPayload reportPayload);
    ReportEntity setChildReferenceFieldsFromPayload(BugBountyReportPayload reportPayload, ReportEntity report);
    void saveCollaborators(List<CollaboratorPayload> collaboratorDTOs, ReportEntity report);
    ReportEntity saveForReportType(ReportEntity report);

    /////////////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    // manual
    ReportManual createReportManualFromPayload(ReportManualPayload reportPayload);

    // CVSSReport
    ReportCVSS createReportCVSSFromPayload(ReportCVSSPayload reportPayload);
    void setCVSSFields(ReportCVSS reportCVSS,ReportCVSSPayload reportPayload);

    //


}
