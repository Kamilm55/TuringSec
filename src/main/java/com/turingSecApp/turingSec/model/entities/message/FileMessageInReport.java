package com.turingSecApp.turingSec.model.entities.message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
//@DiscriminatorValue("string")
@Table(name = "messageInReport_file")
public class FileMessageInReport extends BaseMessageInReport {
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(columnDefinition = "BYTEA", nullable = false)
    private byte[] fileData;

    @Column(nullable = false)
    private String fileLocation;

}

