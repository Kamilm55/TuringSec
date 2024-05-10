package com.turingSecApp.turingSec.helper.entityHelper;

import com.turingSecApp.turingSec.dao.entities.ReportEntity;
import com.turingSecApp.turingSec.dao.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportEntityHelper implements IReportEntityHelper {
    private final CollaboratorRepository collaboratorRepository;
    private final ReportsRepository reportsRepository;

    public ReportEntity deleteReportChildEntities(ReportEntity report) {
        // Delete children repo first
        collaboratorRepository.deleteAll(report.getCollaborators());

        // Delete all child entities of the report
        report.getCollaborators().clear(); // Assuming this is the child entity

        // Add more child entity deletion logic here if needed

       return reportsRepository.save(report);
    }
}
