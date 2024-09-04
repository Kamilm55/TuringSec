package com.turingSecApp.turingSec.response.report;

import com.turingSecApp.turingSec.model.entities.report.CollaboratorEntity;
import com.turingSecApp.turingSec.model.entities.report.embedded.*;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORCOMPANY;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportDTO {
    // Base(Parent) Report fields
    private Long id;
    private String room;

    private ReportWeakness weakness;

    private ReportAsset asset;
    private ProofOfConcept proofOfConcept;
    private DiscoveryDetails discoveryDetails;

    private List<AttachmentDetails> attachments = new ArrayList<>();

    private String methodName;
    private Date lastActivity;
    private String rewardsStatus;
    private String reportTemplate;
    private LocalDate createdAt;
    private List<CollaboratorEntity> collaborators = new ArrayList<>();
    private REPORTSTATUSFORUSER statusForUser;
    private REPORTSTATUSFORCOMPANY statusForCompany;

    // User fields
    private String userId;
    private String username;
    private String userEmail;

    // Program field
    private Long programId;

    // Company fields
    private String companyId;
    private String companyName;
    private String companyEmail;
}
