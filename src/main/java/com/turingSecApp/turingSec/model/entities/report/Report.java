package com.turingSecApp.turingSec.model.entities.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.model.entities.program.Program;
import com.turingSecApp.turingSec.model.entities.report.embedded.*;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORCOMPANY;
import com.turingSecApp.turingSec.model.entities.report.enums.REPORTSTATUSFORUSER;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.DoubleStream;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude = {"user", "bugBountyProgram", "collaborators","asset","weakness"})
@ToString(exclude = {"user", "bugBountyProgram", "collaborators","asset","weakness"})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "report_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "bug_bounty_reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //@Column(updatable = false, nullable = false, unique = true) //todo: @PrePersist not work in prod
    private String room;

    @Embedded
    private ReportWeakness weakness;
    @Embedded
    private ReportAsset asset;
    @Embedded
    private ProofOfConcept proofOfConcept;
    @Embedded
    private DiscoveryDetails discoveryDetails;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // only auto-delete (cascading) , because in unidirectional i should delete medias manually
    private List<Media> media = new ArrayList<>();

    @ElementCollection
    private List<AttachmentDetails> attachments = new ArrayList<>();

    private String methodName;
    private Date lastActivity;
    private String rewardsStatus;
    private String reportTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private UserEntity user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_bounty_program_id")
    @JsonIgnore
    private Program bugBountyProgram;

 //   private Double ownPercentage = 100.0; // Default own percentage is 100%

    @OneToMany(mappedBy = "bugBountyReport", cascade = CascadeType.ALL/*,orphanRemoval = true*/)
    private List<CollaboratorEntity> collaborators = new ArrayList<>();

    private REPORTSTATUSFORUSER statusForUser;// hacker hissesinde all( submitted underreview (accepted | rejected) -> assessed )

    private REPORTSTATUSFORCOMPANY statusForCompany;//sirket hissesinde all(unreviewed,reviewed,assessed)

    //  User - Company
    // user report atannan sonra  ---> submitted - unreviewed
    // company reporta tiklasa ----> underreview - reviewed
    // company reportu deyerlendirir --->
    // accepted - assessed
    // rejected - assessed

    @PrePersist
    public void prePersist() {
        // Generate UUID for room or perform any other pre-persist actions
        if (this.room == null) {
            this.room = UUID.randomUUID().toString();
        }
    }
    public void addCollaborator(CollaboratorEntity collaborator) {
        collaborators.add(collaborator);
        collaborator.setBugBountyReport(this);
    }
    public void addAttachment(AttachmentDetails attachmentDetails) {
        attachments.add(attachmentDetails);
    }

}
