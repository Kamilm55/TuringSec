package com.turingSecApp.turingSec.response.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.BugBountyProgramEntity;
import com.turingSecApp.turingSec.dao.entities.CollaboratorEntity;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import jakarta.persistence.*;
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
    private Long bugBountyProgramId;

    private Double ownPercentage;
    private List<CollaboratorEntity> collaborators;
}
