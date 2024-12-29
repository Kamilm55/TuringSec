package com.turingSecApp.turingSec.file_upload.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "background_image_for_company")
public class BackgroundImageForCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private String contentType;

    @Column(columnDefinition = "BYTEA")
    private byte[] fileData;

    private String companyId;
}
