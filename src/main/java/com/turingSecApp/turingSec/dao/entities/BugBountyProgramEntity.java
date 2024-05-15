package com.turingSecApp.turingSec.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.report.ReportEntity;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = {"company", "assetTypes", "prohibits","reports"})
@ToString(exclude = {"company", "assetTypes", "prohibits","reports"})
@Entity
@Table(name = "bug_bounty_programs")
public class BugBountyProgramEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column
    private LocalDate toDate;

    @Column
    private String notes;

    @Column
    private String policy;

    @ElementCollection
    private List<String> inScope = new ArrayList<>();

    @ElementCollection
    private List<String> outOfScope = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private CompanyEntity company;

    @OneToMany(mappedBy = "bugBountyProgram",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReportEntity> reports = new ArrayList<>();

    @OneToMany(mappedBy = "bugBountyProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetTypeEntity> assetTypes = new ArrayList<>();


    @OneToMany(mappedBy = "bugBountyProgramForStrict", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StrictEntity> prohibits = new ArrayList<>();

    // Getters and setters...

    public void removeReport(Long reportId) {
        if (reports != null) {
            reports.removeIf(report -> report.getId().equals(reportId));

        }
    }
}