    package com.turingSecApp.turingSec.dao.entities.report.embedded;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.*;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ReportAssetEntity {
        private String assetName;
        private String assetType;

    //    private String level;
    //    private Double price;
    }
