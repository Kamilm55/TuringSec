package com.turingSecApp.turingSec.model.entities.program;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turingSecApp.turingSec.model.entities.program.asset.ProgramAsset;
import com.turingSecApp.turingSec.model.entities.user.CompanyEntity;
import com.turingSecApp.turingSec.model.entities.report.Report;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = {"company", "asset", "prohibits","reports"})
@ToString(exclude = {"company", "asset", "prohibits","reports"})
@Entity
@Table(name = "bug_bounty_programs")
@Slf4j
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fromDate;

    @Column(nullable = false)
    private LocalDate toDate;

    @Transient
    private Long lastDays;

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
    private List<Prohibit> prohibits = new ArrayList<>();

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

    // Other approach --> @PostLoad in JPA -> In this example, the calculateLastDays() method will be called automatically after the entity is loaded from the database, setting the lastDays field.
    public Long getLastDays() {
        if (fromDate != null && toDate != null) {
            return ChronoUnit.DAYS.between(fromDate, toDate);
        }
        log.error("FromDate,toDate are null");
        return null;
    }

//    @PostLoad
//    public void calculateLastDays() {
//        if (fromDate != null && toDate != null) {
//            lastDays = ChronoUnit.DAYS.between(fromDate, toDate);
//        } else {
//            lastDays = null;
//        }
//    }


}