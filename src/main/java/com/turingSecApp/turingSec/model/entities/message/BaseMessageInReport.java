package com.turingSecApp.turingSec.model.entities.message;

import com.turingSecApp.turingSec.model.entities.report.Report;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "messageInReport_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "base_message_in_report")
public class BaseMessageInReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // id as PK and FK(reply_to_id)

//    @Column(nullable = false)
//    private String room; // todo: in report entity must be include generated  uuid room , 5 , company sekil  user sekil
//                                  therefore messaging, socket connection is specific for report , when send msg --> getAllReports -> send to which the report room is

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime editedAt;

    @Column(nullable = false)
    private boolean isEdited;

    @Column(nullable = false)
    private boolean isReplied;

    @OneToOne
    @JoinColumn(name = "reply_to_id")// This will be the foreign key column in the same table
    private BaseMessageInReport replyTo;

    @OneToOne
    private Report report;

    // user
    // company

}
