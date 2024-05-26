    package com.turingSecApp.turingSec.dao.entities.report.embedded;

    import jakarta.persistence.*;
    import lombok.*;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ReportAsset {
        private String assetName;
        private String assetType; // -> todo: Set<type>

    //    private String level;
    //    private Double price;
    }
