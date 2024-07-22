package com.turingSecApp.turingSec.response.message;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty(value = "isEdited")
    private boolean isEdited;

    @JsonProperty(value = "isReplied")
    private boolean isReplied;

    private Long replyToId;
//    private String replyToMsgCreatedAt;

    private Long reportId;

    @JsonProperty(value = "isHacker")
    private boolean isHacker;

    private Long userId;
    private Long companyId; // change to CompanyEntity
    private Long programId;// change to Program entity

    // user img,bck img
}
