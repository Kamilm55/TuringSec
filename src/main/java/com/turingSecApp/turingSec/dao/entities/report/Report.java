package com.turingSecApp.turingSec.dao.entities.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.program.Program;
import com.turingSecApp.turingSec.dao.entities.report.embedded.DiscoveryDetails;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ProofOfConcept;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ReportAsset;
import com.turingSecApp.turingSec.dao.entities.report.embedded.ReportWeakness;
import com.turingSecApp.turingSec.dao.entities.user.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    private List<String> attachments = new ArrayList<>();

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

    private Double ownPercentage = 100.0; // Default own percentage is 100%

    @OneToMany(mappedBy = "bugBountyReport", cascade = CascadeType.ALL)
    private List<CollaboratorEntity> collaborators = new ArrayList<>();


    public void addCollaborator(CollaboratorEntity collaborator) {
        collaborators.add(collaborator);
        collaborator.setBugBountyReport(this);
    }
    public void addAttachment(String media_link) {
        attachments.add(media_link);
    }
}
