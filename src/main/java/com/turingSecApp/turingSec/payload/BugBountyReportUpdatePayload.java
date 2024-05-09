package com.turingSecApp.turingSec.payload;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "collaborator")
@ToString(exclude = "collaborator")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugBountyReportUpdatePayload {
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

    // collaborator is required, but its elements' validations are handled in CollaboratorWithIdPayload
    @NotNull(message = "Collaborator list is required")
    private List<CollaboratorWithIdPayload> collaborator;
}
