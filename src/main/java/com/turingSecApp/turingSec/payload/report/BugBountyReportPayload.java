package com.turingSecApp.turingSec.payload.report;

import com.turingSecApp.turingSec.dao.entities.report.embedded.DiscoveryDetails;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ProofOfConcept;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ReportWeakness;
import com.turingSecApp.turingSec.payload.report.child.CollaboratorPayload;
import com.turingSecApp.turingSec.payload.report.child.ReportAssetPayload;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "collaboratorPayload")
@ToString(exclude = "collaboratorPayload")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugBountyReportPayload {
    @NotNull(message = "Last Activity is required")
    private Date lastActivity;

    @NotBlank(message = "Rewards Status is required")
    private String rewardsStatus;
    @NotBlank(message = "Report template is required")
    private String reportTemplate;

    @NotNull(message = "Own percentage of work is required")
    private Double ownPercentage = 100.0; // Default own percentage is 100%
    private List<CollaboratorPayload> collaboratorPayload = new ArrayList<>();

    @NotNull(message = "Asset is required")
    private @Valid ReportAssetPayload reportAssetPayload;

    @NotNull(message = "Weakness is required")
    private @Valid ReportWeakness weakness;

    @NotNull(message = "Proof of concept is required")
    private @Valid ProofOfConcept proofOfConcept;

    private @Valid DiscoveryDetails discoveryDetails;



    // methodName is optional
    private String methodName;

}
