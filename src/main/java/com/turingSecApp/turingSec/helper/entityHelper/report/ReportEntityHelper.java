package com.turingSecApp.turingSec.helper.entityHelper.report;

import com.turingSecApp.turingSec.dao.entities.report.CollaboratorEntity;
import com.turingSecApp.turingSec.dao.entities.report.ReportCVSS;
import com.turingSecApp.turingSec.dao.entities.report.ReportManual;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ReportAsset;
import com.turingSecApp.turingSec.dao.entities.report.Report;
import com.turingSecApp.turingSec.dao.repository.*;
import com.turingSecApp.turingSec.dao.repository.program.ProgramsRepository;
import com.turingSecApp.turingSec.dao.repository.report.ReportCVSSRepository;
import com.turingSecApp.turingSec.dao.repository.report.ReportManualRepository;
import com.turingSecApp.turingSec.dao.repository.report.ReportsRepository;
import com.turingSecApp.turingSec.exception.custom.UserNotFoundException;
import com.turingSecApp.turingSec.helper.entityHelper.report.IReportEntityHelper;
import com.turingSecApp.turingSec.payload.report.BugBountyReportPayload;
import com.turingSecApp.turingSec.payload.report.ReportCVSSPayload;
import com.turingSecApp.turingSec.payload.report.ReportManualPayload;
import com.turingSecApp.turingSec.payload.report.child.CollaboratorPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportEntityHelper implements IReportEntityHelper {
    private final ReportsRepository reportsRepository;
    private final ReportManualRepository reportManualRepository;
    private final ReportCVSSRepository reportCVSSRepository;

    private final CollaboratorRepository collaboratorRepository;
    private final ReportsRepository bugBountyReportRepository;
    private final UserRepository userRepository;
    private final ProgramsRepository programsRepository;

    //todo: delete auto , not manually
    public Report deleteReportChildEntities(Report report) {
        // Delete children repo first
        collaboratorRepository.deleteAll(report.getCollaborators());

        // Delete all child entities of the report
        report.getCollaborators().clear(); // Assuming this is the child entity

        // Add more child entity deletion logic here if needed

       return reportsRepository.save(report);
    }

    @Override
    public Report createReportsEntityFromPayload(BugBountyReportPayload reportPayload) {
        Report report = new Report();
        setCommonReportProperties(report, reportPayload);
        return report;
    }

    @Override
    public ReportManual createReportManualFromPayload(ReportManualPayload reportPayload) {
        ReportManual reportManual = new ReportManual();
        setCommonReportProperties(reportManual,reportPayload);
        reportManual.setSeverity(reportPayload.getSeverity());

        return reportManual;
    }

    @Override
    public ReportCVSS createReportCVSSFromPayload(ReportCVSSPayload reportPayload) {

        ReportCVSS reportCVSS = new ReportCVSS();
        setCommonReportProperties(reportCVSS,reportPayload);

        setCVSSFields(reportCVSS,reportPayload);

        return reportCVSS;
    }

    @Override
    public void setCVSSFields(ReportCVSS reportCVSS,ReportCVSSPayload reportPayload){
        reportCVSS.setAttackVector(reportPayload.getAttackVector());
        reportCVSS.setAttackComplexity(reportPayload.getAttackComplexity());
        reportCVSS.setPrivilegesRequired(reportPayload.getPrivilegesRequired());
        reportCVSS.setUserInteractions(reportPayload.getUserInteractions());
        reportCVSS.setScope(reportPayload.getScope());
        reportCVSS.setConfidentiality(reportPayload.getConfidentiality());
        reportCVSS.setIntegrity(reportPayload.getIntegrity());
        reportCVSS.setAvailability(reportPayload.getAvailability());
    }
    @Override
    public void setCommonReportProperties(Report report, BugBountyReportPayload reportPayload) {
        // Set basic type fields or embeddable
        report.setAsset(new ReportAsset(reportPayload.getReportAssetPayload().getAssetName(),reportPayload.getReportAssetPayload().getAssetType()));
        report.setWeakness(reportPayload.getWeakness());
        report.setProofOfConcept(reportPayload.getProofOfConcept());
        report.setDiscoveryDetails(reportPayload.getDiscoveryDetails());

//        report.setSeverity(reportPayload.getSeverity());
        report.setLastActivity(reportPayload.getLastActivity());
        report.setRewardsStatus(reportPayload.getRewardsStatus());
        report.setOwnPercentage(reportPayload.getOwnPercentage());
        report.setReportTemplate(reportPayload.getReportTemplate());
        report.setMethodName(reportPayload.getMethodName());
    }

    @Override
    public Report setChildReferenceFieldsFromPayload(BugBountyReportPayload reportPayload, Report report) {
        // Save the report and its collaborators
        saveCollaborators(reportPayload.getCollaboratorPayload(), report);

        return saveForReportType(report);
    }

    @Override
    public Report saveForReportType(Report report) {
        Report report1 = null;

        if(report instanceof ReportManual reportManual){
             report1 = reportManualRepository.save(reportManual);
        }else if(report instanceof ReportCVSS reportCVSS) {
            report1 = reportCVSSRepository.save(reportCVSS);
        }
        return report1;
    }

    @Override
    public void saveCollaborators(List<CollaboratorPayload> collaboratorDTOs, Report report) {
        for (var collaboratorDTO : collaboratorDTOs) {
            CollaboratorEntity collaboratorEntity = new CollaboratorEntity();
            userRepository.findByUsername(collaboratorDTO.getHackerUsername()).orElseThrow(() -> new UserNotFoundException("User with username '" + collaboratorDTO.getHackerUsername() + "' not found for collaborating"));
            collaboratorEntity.setCollaborationPercentage(collaboratorDTO.getCollaborationPercentage());
            collaboratorEntity.setHackerUsername(collaboratorDTO.getHackerUsername());
            collaboratorEntity.setBugBountyReport(report);

            collaboratorRepository.save(collaboratorEntity);

            report.addCollaborator(collaboratorEntity);
        }
        saveForReportType(report);
    }

}
