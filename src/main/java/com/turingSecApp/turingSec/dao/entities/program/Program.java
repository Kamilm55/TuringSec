package com.turingSecApp.turingSec.dao.entities.program;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.dao.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.dao.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.dao.entities.report.Report;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = {"company", "asset", "prohibits","reports"})
@ToString(exclude = {"company", "asset", "prohibits","reports"})
@Entity
@Table(name = "bug_bounty_programs")
public class Program {
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

    @OneToMany(mappedBy = "bugBountyProgram",fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "bugBountyProgramForStrict", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StrictEntity> prohibits = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private CompanyEntity company;

    @OneToOne(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private ProgramAsset asset;

     // Getters and setters...

    public void removeReport(Long reportId) {
        if (reports != null) {
            reports.removeIf(report -> report.getId().equals(reportId));

        }
    }
}