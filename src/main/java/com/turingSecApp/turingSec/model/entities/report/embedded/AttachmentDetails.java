package com.turingSecApp.turingSec.model.entities.report.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDetails {
    private String url;
    private String contentType;
}
