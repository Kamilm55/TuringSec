package com.turingSecApp.turingSec.model.entities.message;

import com.turingSecApp.turingSec.model.entities.report.Report;
import com.turingSecApp.turingSec.model.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private String type;
    private LocalDateTime sendTime;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private Report report;
}
