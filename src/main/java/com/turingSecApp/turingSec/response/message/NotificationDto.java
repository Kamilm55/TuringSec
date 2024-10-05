package com.turingSecApp.turingSec.response.message;

import com.turingSecApp.turingSec.model.entities.report.Report;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private String type;
    private LocalDateTime sendTime;

    private Report report;
}
