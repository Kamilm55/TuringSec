package com.turingSecApp.turingSec.response.message;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class StringMessageInReportDTO extends BaseMessageInReportDTO {
    private String content;
   // private String replyToContent;
}
