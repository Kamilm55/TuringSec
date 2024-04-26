package com.turingSecApp.turingSec.payload;

import com.turingSecApp.turingSec.dao.entities.CollaboratorEntity;
import com.turingSecApp.turingSec.response.CollaboratorDTO;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "collaborator")
@ToString(exclude = "collaborator")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugBountyReportUpdatePayload {
    private String asset;
    private String weakness;
    private String severity;
    private String methodName;
    private String proofOfConcept;
    private String discoveryDetails;
    private Date lastActivity;
    private String reportTitle;
    private String rewardsStatus;
    private String vulnerabilityUrl;

    private Long userId;

    private List<CollaboratorWithIdPayload> collaborator;
}
