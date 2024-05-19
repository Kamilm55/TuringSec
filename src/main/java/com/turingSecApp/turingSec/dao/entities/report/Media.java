package com.turingSecApp.turingSec.dao.entities.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contentType;

    @Column(columnDefinition = "BYTEA")
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "bug_bounty_report_id")
    @JsonIgnore
    private ReportEntity report;

    private Long hackerId;
}
