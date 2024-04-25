package com.turingSecApp.turingSec.payload;

import com.turingSecApp.turingSec.response.CollaboratorDTO;
import jakarta.persistence.Column;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "CollaboratorDTO")
@ToString(exclude = "CollaboratorDTO")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BugBountyReportPayload {
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

    private List<CollaboratorDTO> collaboratorDTO;

}
