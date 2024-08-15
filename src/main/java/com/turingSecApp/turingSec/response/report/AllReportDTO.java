package com.turingSecApp.turingSec.response.report;

import com.turingSecApp.turingSec.model.entities.report.CollaboratorEntity;
import com.turingSecApp.turingSec.model.entities.report.embedded.DiscoveryDetails;
import com.turingSecApp.turingSec.model.entities.report.embedded.ProofOfConcept;
import com.turingSecApp.turingSec.model.entities.report.embedded.ReportAsset;
import com.turingSecApp.turingSec.model.entities.report.embedded.ReportWeakness;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"collaborators"})
@ToString(exclude = {"collaborators"})
public class AllReportDTO {
    private Long id;
    private String severity;
    private String methodName;
    private Date lastActivity;
    private String rewardsStatus;
    private String reportTemplate;

    private Long userId;
    private Long bugBountyProgramId;

    private Double ownPercentage;
    private List<CollaboratorEntity> collaborators;

    private /*ReportAssetDTO*/ ReportAsset reportAsset;
    private ReportWeakness weakness;
    private ProofOfConcept proofOfConcept;
    private DiscoveryDetails discoveryDetails;

    private Long companyId;
    private Long programId;
    private String userName;
}
