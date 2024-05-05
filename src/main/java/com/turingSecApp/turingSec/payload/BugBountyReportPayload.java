package com.turingSecApp.turingSec.payload;

import com.turingSecApp.turingSec.response.CollaboratorDTO;
import jakarta.persistence.Column;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = "collaboratorDTO")
@ToString(exclude = "collaboratorDTO")
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

    private List<CollaboratorDTO> collaboratorDTO = new ArrayList<>();

}
