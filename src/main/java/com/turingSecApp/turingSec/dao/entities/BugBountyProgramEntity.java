package com.turingSecApp.turingSec.dao.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(exclude = {"company", "assetTypes", "prohibits"})
@ToString(exclude = {"company", "assetTypes", "prohibits"})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonIgnore
    private CompanyEntity company;

    @OneToMany(mappedBy = "bugBountyProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetTypeEntity> assetTypes = new ArrayList<>();


    @OneToMany(mappedBy = "bugBountyProgramForStrict", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StrictEntity> prohibits = new ArrayList<>();

    // Getters and setters
}