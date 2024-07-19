package com.turingSecApp.turingSec.response.message;

import com.turingSecApp.turingSec.model.entities.message.BaseMessageInReport;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@EqualsAndHashCode
@ToString
@Data
public class BaseMessageInReportDTO {
    private Long id; // id as PK and FK(reply_to_id)

    private String createdAt;

    private String editedAt;

    private boolean isEdited;

    private boolean isReplied;

    private Long replyToId;
//    private String replyToMsgCreatedAt;

    private Long reportId;

    // user img
    // isHacker?
}
