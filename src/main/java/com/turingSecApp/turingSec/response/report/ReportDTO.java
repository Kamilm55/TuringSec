package com.turingSecApp.turingSec.response.report;

import com.turingSecApp.turingSec.dao.entities.report.*;
import com.turingSecApp.turingSec.dao.entities.report.embedded.DiscoveryDetails;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ProofOfConcept;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ReportAssetEntity;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ReportWeakness;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"collaborators"})
@ToString(exclude = {"collaborators"})
public class ReportDTO {
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

    private /*ReportAssetDTO*/ ReportAssetEntity reportAsset;
    private ReportWeakness weakness;
    private ProofOfConcept proofOfConcept;
    private DiscoveryDetails discoveryDetails;
}
