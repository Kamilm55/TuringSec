package com.turingSecApp.turingSec.payload.report;

import com.turingSecApp.turingSec.response.report.CollaboratorPayload;
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
    @NotBlank(message = "Asset is required")
    private String asset;

    @NotBlank(message = "Weakness is required")
    private String weakness;

    @NotBlank(message = "Severity is required")
    private String severity;

    @NotBlank(message = "Proof of Concept is required")
    private String proofOfConcept;

    @NotBlank(message = "Discovery Details is required")
    private String discoveryDetails;

    @NotNull(message = "Last Activity is required")
    private Date lastActivity;

    @NotBlank(message = "Report Title is required")
    private String reportTitle;

    @NotBlank(message = "Rewards Status is required")
    private String rewardsStatus;

    // vulnerabilityUrl is optional
    private String vulnerabilityUrl;
    // methodName is optional
    private String methodName;

//    @NotNull(message = "User Id is required")
//    private Long userId;

    private Double ownPercentage = 100.0; // Default own percentage is 100%
    private List<CollaboratorPayload> collaboratorPayload = new ArrayList<>();
}
